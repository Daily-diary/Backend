package com.likelion.daily_diary.domain.diary.dto;

import java.time.LocalDate;
import java.util.List;

public record DiaryRequestDto(
        String title,
        String content,
        String mood,
        boolean isPublic,
        LocalDate diaryDate,
        List<String> imageUrls
) {}
