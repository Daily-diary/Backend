package com.likelion.daily_diary.domain.diary.repository;

import com.likelion.daily_diary.domain.diary.entity.DiaryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiaryImageRepository extends JpaRepository<DiaryImage, UUID> {
}
