package com.likelion.daily_diary.domain.friendship.controller;

import com.likelion.daily_diary.domain.friendship.dto.FriendRequestResponse;
import com.likelion.daily_diary.domain.friendship.dto.FriendResponse;
import com.likelion.daily_diary.domain.friendship.service.FriendshipService;
import com.likelion.daily_diary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping("/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(
            @AuthenticationPrincipal User me,
            @PathVariable UUID memberId
    ) {
        friendshipService.sendRequest(me, memberId);
    }

    @GetMapping
    public List<FriendResponse> getFriends(@AuthenticationPrincipal User me) {
        return friendshipService.getFriends(me);
    }

    @GetMapping("/requests/received")
    public List<FriendRequestResponse> getReceivedRequests(@AuthenticationPrincipal User me) {
        return friendshipService.getReceivedRequests(me);
    }

    @GetMapping("/requests/sent")
    public List<FriendRequestResponse> getSentRequests(@AuthenticationPrincipal User me) {
        return friendshipService.getSentRequests(me);
    }

    @PatchMapping("/{friendshipId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptRequest(
            @AuthenticationPrincipal User me,
            @PathVariable UUID friendshipId
    ) {
        friendshipService.acceptRequest(me, friendshipId);
    }

    @PatchMapping("/{friendshipId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(
            @AuthenticationPrincipal User me,
            @PathVariable UUID friendshipId
    ) {
        friendshipService.rejectRequest(me, friendshipId);
    }

    @DeleteMapping("/requests/{friendshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRequest(
            @AuthenticationPrincipal User me,
            @PathVariable UUID friendshipId
    ) {
        friendshipService.cancelRequest(me, friendshipId);
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(
            @AuthenticationPrincipal User me,
            @PathVariable UUID memberId
    ) {
        friendshipService.removeFriend(me, memberId);
    }
}
