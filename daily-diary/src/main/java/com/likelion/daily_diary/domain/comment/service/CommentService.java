package com.likelion.daily_diary.domain.comment.service;

import com.likelion.daily_diary.domain.comment.dto.CommentRequestDto;
import com.likelion.daily_diary.domain.comment.dto.CommentResponseDto;
import com.likelion.daily_diary.domain.comment.entity.DiaryComment;
import com.likelion.daily_diary.domain.comment.repository.DiaryCommentRepository;
import com.likelion.daily_diary.domain.diary.entity.Diary;
import com.likelion.daily_diary.domain.diary.repository.DiaryRepository;
import com.likelion.daily_diary.domain.friendship.repository.FriendshipRepository;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final DiaryCommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(User user, UUID diaryId) {
        Diary diary = diaryRepository.findPublicById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 비공개 일기입니다."));
        if (!diary.getUser().equals(user) && !friendshipRepository.areFriends(user, diary.getUser())) {
            throw new IllegalArgumentException("친구의 일기만 볼 수 있습니다.");
        }
        return commentRepository.findByDiaryOrderByCreatedAtAsc(diary)
                .stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public CommentResponseDto addComment(User user, UUID diaryId, CommentRequestDto request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        Diary diary = diaryRepository.findPublicById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 비공개 일기입니다."));
        if (!diary.getUser().equals(user) && !friendshipRepository.areFriends(user, diary.getUser())) {
            throw new IllegalArgumentException("친구의 일기에만 이야기를 남길 수 있습니다.");
        }
        DiaryComment comment = DiaryComment.builder()
                .user(user)
                .diary(diary)
                .content(request.content())
                .build();
        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(User user, UUID diaryId, UUID commentId) {
        DiaryComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!comment.getDiary().getId().equals(diaryId)) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("본인의 이야기만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }
}
