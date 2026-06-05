package com.likelion.daily_diary.config;

import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("default")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByFirebaseUid("test-uid-123").isEmpty()) {
            userRepository.save(User.builder()
                    .firebaseUid("test-uid-123")
                    .username("테스트유저")
                    .email("test@test.com")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
    }
}
