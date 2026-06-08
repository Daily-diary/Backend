package com.likelion.daily_diary.domain.feed.controller;

import com.likelion.daily_diary.domain.feed.dto.FeedResponseDto;
import com.likelion.daily_diary.domain.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: JWT 필터 연동 후 @RequestHeader 대신 @AuthenticationPrincipal로 교체
@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getFeed(
            @RequestHeader("X-Firebase-UID") String firebaseUid) {
        return ResponseEntity.ok(feedService.getFeed(firebaseUid));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<FeedResponseDto>> getFriendFeed(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(feedService.getFriendFeed(firebaseUid, userId));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<FeedResponseDto> getFeedDetail(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @PathVariable UUID diaryId) {
        return ResponseEntity.ok(feedService.getFeedDetail(firebaseUid, diaryId));
    }
}
