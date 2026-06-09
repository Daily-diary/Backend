package com.likelion.daily_diary.domain.user.controller;

import com.likelion.daily_diary.domain.user.dto.ProfileImageUpdateRequest;
import com.likelion.daily_diary.domain.user.dto.ProfileUpdateRequest;
import com.likelion.daily_diary.domain.user.dto.UserResponse;
import com.likelion.daily_diary.domain.user.dto.UserSearchResponse;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMyProfile(@AuthenticationPrincipal User user) {
        return UserResponse.from(user);
    }

    @GetMapping("/search")
    public List<UserSearchResponse> searchMembers(
            @AuthenticationPrincipal User user,
            @RequestParam String q
    ) {
        return userService.searchUsers(user, q);
    }

    @PatchMapping("/me")
    public UserResponse updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody ProfileUpdateRequest request
    ) {
        return UserResponse.from(userService.updateNickname(user.getId(), request.nickname()));
    }

    @PatchMapping("/me/profile-image")
    public UserResponse updateProfileImage(
            @AuthenticationPrincipal User user,
            @RequestBody ProfileImageUpdateRequest request
    ) {
        return UserResponse.from(userService.updateProfileImage(user.getId(), request.profileImageUrl()));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
    }
}
