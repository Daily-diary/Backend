package com.likelion.daily_diary.domain.comment.repository;

import com.likelion.daily_diary.domain.comment.entity.DiaryComment;
import com.likelion.daily_diary.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment, UUID> {
    List<DiaryComment> findByDiaryOrderByCreatedAtAsc(Diary diary);
    long countByDiary(Diary diary);
}
