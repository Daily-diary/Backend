package com.likelion.daily_diary.domain.diary.repository;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiaryRepository extends JpaRepository<Diary, UUID> {
    List<Diary> findByUserOrderByDiaryDateDesc(User user);

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.user IN :users AND d.isPublic = true ORDER BY d.diaryDate DESC")
    List<Diary> findFeedByUsers(@Param("users") List<User> users);

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.user = :user AND d.isPublic = true ORDER BY d.diaryDate DESC")
    List<Diary> findPublicByUser(@Param("user") User user);

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.id = :id AND d.isPublic = true")
    Optional<Diary> findPublicById(@Param("id") UUID id);
}
