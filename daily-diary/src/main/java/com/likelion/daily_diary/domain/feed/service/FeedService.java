package com.likelion.daily_diary.domain.feed.service;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.repository.DiaryRepository;
import com.likelion.daily_diary.domain.feed.dto.FeedResponseDto;
import com.likelion.daily_diary.domain.friendship.entity.FriendshipStatus;
import com.likelion.daily_diary.domain.friendship.repository.FriendshipRepository;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public List<FeedResponseDto> getFeed(String firebaseUid) {
        User me = findUser(firebaseUid);
        List<User> friends = getAcceptedFriends(me);
        if (friends.isEmpty()) return List.of();
        return diaryRepository.findFeedByUsers(friends)
                .stream()
                .map(FeedResponseDto::from)
                .toList();
    }

    public List<FeedResponseDto> getFriendFeed(String firebaseUid, UUID userId) {
        User me = findUser(firebaseUid);
        User friend = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!friendshipRepository.areFriends(me, friend)) {
            throw new IllegalArgumentException("친구 관계가 아닙니다.");
        }
        return diaryRepository.findPublicByUser(friend)
                .stream()
                .map(FeedResponseDto::from)
                .toList();
    }

    public FeedResponseDto getFeedDetail(String firebaseUid, UUID diaryId) {
        User me = findUser(firebaseUid);
        Diary diary = diaryRepository.findPublicById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 비공개 일기입니다."));
        if (!friendshipRepository.areFriends(me, diary.getUser())) {
            throw new IllegalArgumentException("친구의 일기만 볼 수 있습니다.");
        }
        return FeedResponseDto.from(diary);
    }

    private User findUser(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private List<User> getAcceptedFriends(User user) {
        return friendshipRepository.findByStatusAndUser(FriendshipStatus.ACCEPTED, user)
                .stream()
                .map(f -> f.getRequester().equals(user) ? f.getReceiver() : f.getRequester())
                .toList();
    }
}
