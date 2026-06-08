package com.likelion.daily_diary.member.dto;

import com.likelion.daily_diary.member.Member;

public record MemberSearchResponse(
        Long memberId,
        String nickname,
        String email,
        String profileImageUrl,
        String friendshipStatus  // NONE | FRIEND | SENT | RECEIVED
) {

    public static MemberSearchResponse of(Member member, String status) {
        return new MemberSearchResponse(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                member.getProfileImageUrl(),
                status
        );
    }
}
