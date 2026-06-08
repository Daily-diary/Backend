package com.likelion.daily_diary.friendship;

import com.likelion.daily_diary.friendship.dto.FriendRequestResponse;
import com.likelion.daily_diary.friendship.dto.FriendResponse;
import com.likelion.daily_diary.member.Member;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // 친구 요청 보내기
    @PostMapping("/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(
            @AuthenticationPrincipal Member me,
            @PathVariable Long memberId
    ) {
        friendshipService.sendRequest(me, memberId);
    }

    // 친구 목록 조회
    @GetMapping
    public List<FriendResponse> getFriends(@AuthenticationPrincipal Member me) {
        return friendshipService.getFriends(me);
    }

    // 받은 친구 요청 목록
    @GetMapping("/requests/received")
    public List<FriendRequestResponse> getReceivedRequests(@AuthenticationPrincipal Member me) {
        return friendshipService.getReceivedRequests(me);
    }

    // 보낸 친구 요청 목록
    @GetMapping("/requests/sent")
    public List<FriendRequestResponse> getSentRequests(@AuthenticationPrincipal Member me) {
        return friendshipService.getSentRequests(me);
    }

    // 친구 요청 수락
    @PatchMapping("/{friendshipId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptRequest(
            @AuthenticationPrincipal Member me,
            @PathVariable Long friendshipId
    ) {
        friendshipService.acceptRequest(me, friendshipId);
    }

    // 친구 요청 거절
    @PatchMapping("/{friendshipId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(
            @AuthenticationPrincipal Member me,
            @PathVariable Long friendshipId
    ) {
        friendshipService.rejectRequest(me, friendshipId);
    }

    // 친구 요청 취소 (내가 보낸 요청)
    @DeleteMapping("/requests/{friendshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRequest(
            @AuthenticationPrincipal Member me,
            @PathVariable Long friendshipId
    ) {
        friendshipService.cancelRequest(me, friendshipId);
    }

    // 친구 삭제
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(
            @AuthenticationPrincipal Member me,
            @PathVariable Long memberId
    ) {
        friendshipService.removeFriend(me, memberId);
    }
}
