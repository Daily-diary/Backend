package com.likelion.daily_diary.domain.diary.service;

import com.likelion.daily_diary.domain.diary.dto.DiaryRequestDto;
import com.likelion.daily_diary.domain.diary.dto.DiaryResponseDto;
import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.entity.DiaryImage;
import com.likelion.daily_diary.domain.diary.repository.DiaryRepository;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public DiaryResponseDto createDiary(String firebaseUid, DiaryRequestDto request) {
        User user = findUser(firebaseUid);

        Diary diary = Diary.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .mood(request.mood())
                .isPublic(request.isPublic())
                .diaryDate(request.diaryDate() != null ? request.diaryDate() : LocalDate.now())
                .build();

        addImages(diary, request.imageUrls());

        return DiaryResponseDto.from(diaryRepository.save(diary));
    }

    @Transactional(readOnly = true)
    public List<DiaryResponseDto> getMyDiaries(String firebaseUid) {
        User user = findUser(firebaseUid);
        return diaryRepository.findByUserOrderByDiaryDateDesc(user)
                .stream()
                .map(DiaryResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiaryResponseDto getDiary(String firebaseUid, UUID diaryId) {
        Diary diary = findDiary(diaryId);
        validateOwner(diary, firebaseUid);
        return DiaryResponseDto.from(diary);
    }

    public DiaryResponseDto updateDiary(String firebaseUid, UUID diaryId, DiaryRequestDto request) {
        Diary diary = findDiary(diaryId);
        validateOwner(diary, firebaseUid);

        diary.update(request.title(), request.content(), request.mood(), request.isPublic(), request.diaryDate());
        diary.getImages().clear();
        addImages(diary, request.imageUrls());

        return DiaryResponseDto.from(diary);
    }

    public void deleteDiary(String firebaseUid, UUID diaryId) {
        Diary diary = findDiary(diaryId);
        validateOwner(diary, firebaseUid);
        diaryRepository.delete(diary);
    }

    private User findUser(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Diary findDiary(UUID diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));
    }

    private void validateOwner(Diary diary, String firebaseUid) {
        if (!diary.getUser().getFirebaseUid().equals(firebaseUid)) {
            throw new IllegalArgumentException("본인의 일기만 수정/삭제할 수 있습니다.");
        }
    }

    private void addImages(Diary diary, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        for (int i = 0; i < imageUrls.size(); i++) {
            DiaryImage image = DiaryImage.builder()
                    .diary(diary)
                    .imageUrl(imageUrls.get(i))
                    .orderIndex(i)
                    .build();
            diary.getImages().add(image);
        }
    }
}
