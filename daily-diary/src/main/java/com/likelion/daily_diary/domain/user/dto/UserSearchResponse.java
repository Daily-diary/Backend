package com.likelion.daily_diary.domain.user.dto;

import com.likelion.daily_diary.domain.user.entity.User;

import java.util.UUID;

public record UserSearchResponse(
        UUID id,
        String nickname,
        String profileImageUrl,
        String friendshipStatus
) {
    public static UserSearchResponse of(User user, String status) {
        return new UserSearchResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                status
        );
    }
}
