package com.likelion.daily_diary.domain.diary.repository;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiaryRepository extends JpaRepository<Diary, UUID> {
    List<Diary> findByUserOrderByDiaryDateDesc(User user);
}
