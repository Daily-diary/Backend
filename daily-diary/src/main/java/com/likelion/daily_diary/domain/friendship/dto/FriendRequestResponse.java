package com.likelion.daily_diary.domain.friendship.dto;

import com.likelion.daily_diary.domain.friendship.entity.Friendship;
import com.likelion.daily_diary.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendRequestResponse(
        UUID friendshipId,
        UUID memberId,
        String nickname,
        String email,
        String profileImageUrl,
        LocalDateTime requestedAt
) {
    public static FriendRequestResponse fromReceived(Friendship f) {
        User requester = f.getRequester();
        return new FriendRequestResponse(
                f.getId(),
                requester.getId(),
                requester.getNickname(),
                requester.getEmail(),
                requester.getProfileImageUrl(),
                f.getCreatedAt()
        );
    }

    public static FriendRequestResponse fromSent(Friendship f) {
        User receiver = f.getReceiver();
        return new FriendRequestResponse(
                f.getId(),
                receiver.getId(),
                receiver.getNickname(),
                receiver.getEmail(),
                receiver.getProfileImageUrl(),
                f.getCreatedAt()
        );
    }
}
