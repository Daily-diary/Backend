package com.likelion.daily_diary.domain.user.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.likelion.daily_diary.domain.friendship.entity.FriendshipStatus;
import com.likelion.daily_diary.domain.friendship.repository.FriendshipRepository;
import com.likelion.daily_diary.domain.user.dto.UserSearchResponse;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MAX_NICKNAME_LENGTH = 30;

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FirebaseAuth firebaseAuth;

    @Transactional
    public User findOrCreateByFirebaseToken(FirebaseToken token) {
        return userRepository.findByFirebaseUid(token.getUid())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .firebaseUid(token.getUid())
                                .email(token.getEmail())
                                .nickname(normalizeNickname(token.getName(), token.getEmail()))
                                .profileImageUrl(extractPicture(token))
                                .build()
                ));
    }

    @Transactional
    public User updateNickname(UUID userId, String nickname) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.updateNickname(normalizeNickname(nickname, user.getEmail()));
        return user;
    }

    @Transactional
    public User updateProfileImage(UUID userId, String profileImageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.updateProfileImage(profileImageUrl == null || profileImageUrl.isBlank() ? null : profileImageUrl.trim());
        return user;
    }

    @Transactional
    public void deleteUser(User user) {
        User managed = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        try {
            firebaseAuth.deleteUser(managed.getFirebaseUid());
        } catch (FirebaseAuthException e) {
            throw new IllegalStateException("Firebase 회원 삭제에 실패했습니다.", e);
        }
        userRepository.delete(managed);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsers(User me, String query) {
        if (query == null || query.isBlank()) return List.of();
        List<User> results = userRepository.searchByNicknameOrEmail(query.trim(), me.getId());
        return results.stream().map(target -> {
            String status = friendshipRepository.findBetween(me, target)
                    .map(f -> switch (f.getStatus()) {
                        case ACCEPTED -> "FRIEND";
                        case PENDING -> f.getRequester().getId().equals(me.getId()) ? "SENT" : "RECEIVED";
                        case REJECTED -> "NONE";
                    })
                    .orElse("NONE");
            return UserSearchResponse.of(target, status);
        }).toList();
    }

    private String normalizeNickname(String nickname, String email) {
        String value = (nickname == null || nickname.isBlank())
                ? email.substring(0, email.indexOf("@"))
                : nickname.trim();
        return value.length() > MAX_NICKNAME_LENGTH ? value.substring(0, MAX_NICKNAME_LENGTH) : value;
    }

    private String extractPicture(FirebaseToken token) {
        Map<String, Object> claims = token.getClaims();
        Object picture = claims.get("picture");
        if (picture instanceof String pictureUrl && !pictureUrl.isBlank()) {
            return pictureUrl;
        }
        return null;
    }
}
