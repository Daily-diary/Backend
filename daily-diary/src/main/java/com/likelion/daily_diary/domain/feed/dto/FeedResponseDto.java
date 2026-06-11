package com.likelion.daily_diary.domain.feed.dto;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.entity.DiaryImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedResponseDto(
        UUID id,
        String title,
        String content,
        String mood,
        LocalDate diaryDate,
        List<String> imageUrls,
        LocalDateTime createdAt,
        UUID authorId,
        String authorName,
        String authorProfileImageUrl,
        long likeCount,
        boolean liked,
        long commentCount
) {
    public static FeedResponseDto from(Diary diary, long likeCount, boolean liked, long commentCount) {
        return new FeedResponseDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getMood(),
                diary.getDiaryDate(),
                diary.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                diary.getCreatedAt(),
                diary.getUser().getId(),
                diary.getUser().getNickname(),
                diary.getUser().getProfileImageUrl(),
                likeCount,
                liked,
                commentCount
        );
    }
}
