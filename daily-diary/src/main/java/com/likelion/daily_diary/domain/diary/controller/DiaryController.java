package com.likelion.daily_diary.domain.diary.controller;

import com.likelion.daily_diary.domain.diary.dto.DiaryRequestDto;
import com.likelion.daily_diary.domain.diary.dto.DiaryResponseDto;
import com.likelion.daily_diary.domain.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: JWT 필터 연동 후 @RequestHeader 대신 @AuthenticationPrincipal로 교체
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryResponseDto> createDiary(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @RequestBody DiaryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryService.createDiary(firebaseUid, request));
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponseDto>> getMyDiaries(
            @RequestHeader("X-Firebase-UID") String firebaseUid) {
        return ResponseEntity.ok(diaryService.getMyDiaries(firebaseUid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponseDto> getDiary(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @PathVariable UUID id) {
        return ResponseEntity.ok(diaryService.getDiary(firebaseUid, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponseDto> updateDiary(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @PathVariable UUID id,
            @RequestBody DiaryRequestDto request) {
        return ResponseEntity.ok(diaryService.updateDiary(firebaseUid, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @RequestHeader("X-Firebase-UID") String firebaseUid,
            @PathVariable UUID id) {
        diaryService.deleteDiary(firebaseUid, id);
        return ResponseEntity.noContent().build();
    }
}
