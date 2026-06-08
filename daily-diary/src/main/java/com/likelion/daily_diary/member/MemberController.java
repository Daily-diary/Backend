package com.likelion.daily_diary.member;

import com.likelion.daily_diary.member.dto.MemberResponse;
import com.likelion.daily_diary.member.dto.MemberSearchResponse;
import com.likelion.daily_diary.member.dto.ProfileImageUpdateRequest;
import com.likelion.daily_diary.member.dto.ProfileUpdateRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public MemberResponse getMyProfile(@AuthenticationPrincipal Member member) {
        return MemberResponse.from(member);
    }

    @GetMapping("/search")
    public List<MemberSearchResponse> searchMembers(
            @AuthenticationPrincipal Member member,
            @RequestParam String q
    ) {
        return memberService.searchMembers(member, q);
    }

    @PatchMapping("/me")
    public MemberResponse updateMyProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody ProfileUpdateRequest request
    ) {
        return MemberResponse.from(memberService.updateProfile(member, request.nickname()));
    }

    @PatchMapping("/me/profile-image")
    public MemberResponse updateMyProfileImage(
            @AuthenticationPrincipal Member member,
            @RequestBody ProfileImageUpdateRequest request
    ) {
        return MemberResponse.from(memberService.updateProfileImage(member, request.profileImageUrl()));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal Member member) {
        memberService.deleteMember(member);
    }
}
