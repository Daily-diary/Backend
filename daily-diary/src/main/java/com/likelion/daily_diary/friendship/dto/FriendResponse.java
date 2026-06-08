package com.likelion.daily_diary.friendship.dto;

import com.likelion.daily_diary.friendship.Friendship;
import com.likelion.daily_diary.member.Member;

public record FriendResponse(
        Long friendshipId,
        Long memberId,
        String nickname,
        String email,
        String profileImageUrl
) {

    public static FriendResponse from(Friendship friendship, Long myId) {
        Member friend = friendship.getOtherMember(myId);
        return new FriendResponse(
                friendship.getId(),
                friend.getId(),
                friend.getNickname(),
                friend.getEmail(),
                friend.getProfileImageUrl()
        );
    }
}
