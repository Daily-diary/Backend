package com.likelion.daily_diary.domain.friendship.dto;

import com.likelion.daily_diary.domain.friendship.entity.Friendship;
import com.likelion.daily_diary.domain.user.entity.User;

import java.util.UUID;

public record FriendResponse(
        UUID friendshipId,
        UUID userId,
        String nickname,
        String profileImageUrl
) {
    public static FriendResponse of(Friendship friendship, UUID myId) {
        User friend = friendship.getRequester().getId().equals(myId)
                ? friendship.getReceiver()
                : friendship.getRequester();
        return new FriendResponse(
                friendship.getId(),
                friend.getId(),
                friend.getNickname(),
                friend.getProfileImageUrl()
        );
    }
}
