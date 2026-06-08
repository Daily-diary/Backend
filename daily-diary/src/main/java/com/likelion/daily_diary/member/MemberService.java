package com.likelion.daily_diary.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private static final int MAX_NICKNAME_LENGTH = 30;

    private final MemberRepository memberRepository;
    private final FirebaseAuth firebaseAuth;

    public MemberService(MemberRepository memberRepository, FirebaseAuth firebaseAuth) {
        this.memberRepository = memberRepository;
        this.firebaseAuth = firebaseAuth;
    }

    @Transactional
    public Member findOrCreateByFirebaseToken(FirebaseToken token) {
        return memberRepository.findByFirebaseUid(token.getUid())
                .map(this::validateActive)
                .orElseGet(() -> createMember(token));
    }

    @Transactional
    public Member updateProfile(Member member, String nickname) {
        Member currentMember = getActiveMember(member.getId());
        currentMember.updateProfile(normalizeNickname(nickname, currentMember.getEmail()));
        return currentMember;
    }

    @Transactional
    public Member updateProfileImage(Member member, String profileImageUrl) {
        Member currentMember = getActiveMember(member.getId());
        currentMember.updateProfileImage(normalizeOptionalUrl(profileImageUrl));
        return currentMember;
    }

    @Transactional
    public void deleteMember(Member member) {
        Member currentMember = getActiveMember(member.getId());
        try {
            firebaseAuth.deleteUser(currentMember.getFirebaseUid());
        } catch (FirebaseAuthException e) {
            throw new IllegalStateException("Firebase 회원 삭제에 실패했습니다.", e);
        }
        currentMember.softDelete();
    }

    private Member createMember(FirebaseToken token) {
        String email = token.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Firebase 계정에 이메일이 없습니다.");
        }

        String nickname = normalizeNickname(token.getName(), email);
        String profileImageUrl = extractPicture(token);

        Member member = new Member(token.getUid(), email, nickname, profileImageUrl, "GOOGLE");
        return memberRepository.save(member);
    }

    private Member getActiveMember(Long memberId) {
        return memberRepository.findById(memberId)
                .map(this::validateActive)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private Member validateActive(Member member) {
        if (member.isDeleted()) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }
        return member;
    }

    private String normalizeNickname(String nickname, String email) {
        String value = nickname;
        if (value == null || value.isBlank()) {
            value = email.substring(0, email.indexOf("@"));
        }

        value = value.trim();
        if (value.length() > MAX_NICKNAME_LENGTH) {
            value = value.substring(0, MAX_NICKNAME_LENGTH);
        }
        return value;
    }

    private String normalizeOptionalUrl(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }
        return profileImageUrl.trim();
    }

    private String extractPicture(FirebaseToken token) {
        Map<String, Object> claims = token.getClaims();
        Object picture = claims.get("picture");
        if (picture instanceof String pictureUrl && !pictureUrl.isBlank()) {
            return pictureUrl;
        }
        return null;
    }
}
