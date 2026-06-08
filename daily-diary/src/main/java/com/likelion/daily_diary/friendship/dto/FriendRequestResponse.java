package com.likelion.daily_diary.friendship.dto;

import com.likelion.daily_diary.friendship.Friendship;
import com.likelion.daily_diary.member.Member;
import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long friendshipId,
        Long memberId,
        String nickname,
        String email,
        String profileImageUrl,
        LocalDateTime requestedAt
) {

    public static FriendRequestResponse fromReceived(Friendship f) {
        Member requester = f.getRequester();
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
        Member receiver = f.getReceiver();
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
