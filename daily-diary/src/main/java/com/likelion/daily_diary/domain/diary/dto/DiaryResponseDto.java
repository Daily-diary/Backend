package com.likelion.daily_diary.domain.diary.dto;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.entity.DiaryImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DiaryResponseDto(
        UUID id,
        String title,
        String content,
        String mood,
        boolean isPublic,
        LocalDate diaryDate,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DiaryResponseDto from(Diary diary) {
        return new DiaryResponseDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getMood(),
                diary.isPublic(),
                diary.getDiaryDate(),
                diary.getImages().stream()
                        .map(DiaryImage::getImageUrl)
                        .toList(),
                diary.getCreatedAt(),
                diary.getUpdatedAt()
        );
    }
}
