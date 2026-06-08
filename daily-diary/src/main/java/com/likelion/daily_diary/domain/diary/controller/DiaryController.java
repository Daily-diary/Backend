package com.likelion.daily_diary.domain.diary.controller;

import com.likelion.daily_diary.domain.diary.dto.DiaryRequestDto;
import com.likelion.daily_diary.domain.diary.dto.DiaryResponseDto;
import com.likelion.daily_diary.domain.diary.service.DiaryService;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryResponseDto> createDiary(
            @AuthenticationPrincipal User user,
            @RequestBody DiaryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryService.createDiary(user, request));
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponseDto>> getMyDiaries(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(diaryService.getMyDiaries(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponseDto> getDiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(diaryService.getDiary(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponseDto> updateDiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody DiaryRequestDto request) {
        return ResponseEntity.ok(diaryService.updateDiary(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        diaryService.deleteDiary(user, id);
        return ResponseEntity.noContent().build();
    }
}
