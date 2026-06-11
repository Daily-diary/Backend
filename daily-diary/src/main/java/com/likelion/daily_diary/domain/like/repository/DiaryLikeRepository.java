package com.likelion.daily_diary.domain.like.repository;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.like.entity.DiaryLike;
import com.likelion.daily_diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DiaryLikeRepository extends JpaRepository<DiaryLike, UUID> {
    Optional<DiaryLike> findByUserAndDiary(User user, Diary diary);
    long countByDiary(Diary diary);
    boolean existsByUserAndDiary(User user, Diary diary);
}