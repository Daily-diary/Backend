package com.likelion.daily_diary.config;

import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.repository.DiaryRepository;
import com.likelion.daily_diary.domain.friendship.entity.Friendship;
import com.likelion.daily_diary.domain.friendship.entity.FriendshipStatus;
import com.likelion.daily_diary.domain.friendship.repository.FriendshipRepository;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("default")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final DiaryRepository diaryRepository;

    @Override
    public void run(String... args) {
        User me = userRepository.findByFirebaseUid("test-uid-123").orElseGet(() ->
                userRepository.save(User.builder()
                        .firebaseUid("test-uid-123")
                        .nickname("테스트유저")
                        .email("test@test.com")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));

        User friend = userRepository.findByFirebaseUid("test-uid-456").orElseGet(() ->
                userRepository.save(User.builder()
                        .firebaseUid("test-uid-456")
                        .nickname("친구유저")
                        .email("friend@test.com")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));

        if (!friendshipRepository.areFriends(me, friend)) {
            friendshipRepository.save(Friendship.builder()
                    .requester(me)
                    .receiver(friend)
                    .status(FriendshipStatus.ACCEPTED)
                    .build());
        }

        if (diaryRepository.findPublicByUser(friend).isEmpty()) {
            diaryRepository.save(Diary.builder()
                    .user(friend)
                    .title("친구의 공개 일기")
                    .content("오늘은 날씨가 좋았다.")
                    .mood("happy")
                    .isPublic(true)
                    .diaryDate(LocalDate.now())
                    .build());

            diaryRepository.save(Diary.builder()
                    .user(friend)
                    .title("친구의 비공개 일기")
                    .content("이건 비공개라 피드에 안 보여야 함.")
                    .mood("sad")
                    .isPublic(false)
                    .diaryDate(LocalDate.now())
                    .build());
        }
    }
}
