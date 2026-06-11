package com.likelion.daily_diary.domain.like.entity;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diary_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "diary_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}