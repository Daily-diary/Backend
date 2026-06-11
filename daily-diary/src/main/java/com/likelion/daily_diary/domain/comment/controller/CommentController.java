package com.likelion.daily_diary.domain.comment.controller;

import com.likelion.daily_diary.domain.comment.dto.CommentRequestDto;
import com.likelion.daily_diary.domain.comment.dto.CommentResponseDto;
import com.likelion.daily_diary.domain.comment.service.CommentService;
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
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{diaryId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @AuthenticationPrincipal User user,
            @PathVariable UUID diaryId) {
        return ResponseEntity.ok(commentService.getComments(user, diaryId));
    }

    @PostMapping("/{diaryId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @AuthenticationPrincipal User user,
            @PathVariable UUID diaryId,
            @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(commentService.addComment(user, diaryId, request));
    }

    @DeleteMapping("/{diaryId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable UUID diaryId,
            @PathVariable UUID commentId) {
        commentService.deleteComment(user, diaryId, commentId);
        return ResponseEntity.noContent().build();
    }
}
