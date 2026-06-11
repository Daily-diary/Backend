package com.likelion.daily_diary.domain.like.controller;

import com.likelion.daily_diary.domain.like.service.LikeService;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{diaryId}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @AuthenticationPrincipal User user,
            @PathVariable UUID diaryId) {
        boolean liked = likeService.toggleLike(user, diaryId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
