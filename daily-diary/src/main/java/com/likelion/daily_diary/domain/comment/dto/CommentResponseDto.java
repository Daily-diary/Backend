package com.likelion.daily_diary.domain.comment.dto;

import com.likelion.daily_diary.domain.comment.entity.DiaryComment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDto(
        UUID id,
        String content,
        UUID authorId,
        String authorName,
        String authorProfileImageUrl,
        LocalDateTime createdAt
) {
    public static CommentResponseDto from(DiaryComment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageUrl(),
                comment.getCreatedAt()
        );
    }
}
