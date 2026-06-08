package com.likelion.daily_diary.domain.friendship.service;

import com.likelion.daily_diary.domain.friendship.dto.FriendRequestResponse;
import com.likelion.daily_diary.domain.friendship.dto.FriendResponse;
import com.likelion.daily_diary.domain.friendship.entity.Friendship;
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
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendRequest(User me, UUID receiverId) {
        if (me.getId().equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        friendshipRepository.findBetween(me, receiver).ifPresent(f -> {
            throw switch (f.getStatus()) {
                case PENDING -> f.getRequester().getId().equals(me.getId())
                        ? new IllegalStateException("이미 친구 요청을 보냈습니다.")
                        : new IllegalStateException("상대방이 이미 친구 요청을 보냈습니다.");
                case ACCEPTED -> new IllegalStateException("이미 친구입니다.");
                case REJECTED -> new IllegalStateException("거절된 요청입니다.");
            };
        });

        friendshipRepository.save(Friendship.builder()
                .requester(me)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build());
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(User me) {
        return friendshipRepository.findByStatusAndUser(FriendshipStatus.ACCEPTED, me)
                .stream()
                .map(f -> FriendResponse.of(f, me.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(User me) {
        return friendshipRepository.findByReceiverAndStatus(me, FriendshipStatus.PENDING)
                .stream()
                .map(FriendRequestResponse::fromReceived)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getSentRequests(User me) {
        return friendshipRepository.findByRequesterAndStatus(me, FriendshipStatus.PENDING)
                .stream()
                .map(FriendRequestResponse::fromSent)
                .toList();
    }

    @Transactional
    public void acceptRequest(User me, UUID friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getReceiver().getId().equals(me.getId())) {
            throw new IllegalArgumentException("수락 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendship.accept();
    }

    @Transactional
    public void rejectRequest(User me, UUID friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getReceiver().getId().equals(me.getId())) {
            throw new IllegalArgumentException("거절 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void cancelRequest(User me, UUID friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getRequester().getId().equals(me.getId())) {
            throw new IllegalArgumentException("취소 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void removeFriend(User me, UUID friendMemberId) {
        User friend = userRepository.findById(friendMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Friendship friendship = friendshipRepository.findBetween(me, friend)
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
        friendshipRepository.delete(friendship);
    }

    private Friendship getFriendshipOrThrow(UUID friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
    }
}
