package com.likelion.daily_diary.domain.like.service;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.repository.DiaryRepository;
import com.likelion.daily_diary.domain.friendship.repository.FriendshipRepository;
import com.likelion.daily_diary.domain.like.entity.DiaryLike;
import com.likelion.daily_diary.domain.like.repository.DiaryLikeRepository;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final DiaryLikeRepository likeRepository;
    private final DiaryRepository diaryRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public boolean toggleLike(User user, UUID diaryId) {
        Diary diary = diaryRepository.findPublicById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 비공개 일기입니다."));

        if (!diary.getUser().getId().equals(user.getId()) && !friendshipRepository.areFriends(user, diary.getUser())) {
            throw new IllegalArgumentException("친구의 일기만 공감할 수 있습니다.");
        }

        Optional<DiaryLike> existing = likeRepository.findByUserAndDiary(user, diary);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        } else {
            likeRepository.save(DiaryLike.builder().user(user).diary(diary).build());
            return true;
        }
    }
}
