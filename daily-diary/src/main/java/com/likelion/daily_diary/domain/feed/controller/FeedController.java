package com.likelion.daily_diary.domain.feed.controller;

import com.likelion.daily_diary.domain.feed.dto.FeedResponseDto;
import com.likelion.daily_diary.domain.feed.service.FeedService;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getFeed(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.getFeed(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<FeedResponseDto>> getFriendFeed(
            @AuthenticationPrincipal User user,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(feedService.getFriendFeed(user, userId));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<FeedResponseDto> getFeedDetail(
            @AuthenticationPrincipal User user,
            @PathVariable UUID diaryId) {
        return ResponseEntity.ok(feedService.getFeedDetail(user, diaryId));
    }
}
