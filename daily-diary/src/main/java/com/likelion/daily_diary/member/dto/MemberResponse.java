package com.likelion.daily_diary.member.dto;

import com.likelion.daily_diary.member.Member;
import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String email,
        String nickname,
        String profileImageUrl,
        String provider,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getProvider(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
