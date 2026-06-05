package com.likelion.daily_diary.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.likelion.daily_diary.member.Member;
import com.likelion.daily_diary.member.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final FirebaseAuth firebaseAuth;
    private final MemberService memberService;

    public FirebaseAuthFilter(FirebaseAuth firebaseAuth, MemberService memberService) {
        this.firebaseAuth = firebaseAuth;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            try {
                String idToken = authorization.substring(BEARER_PREFIX.length());
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
                Member member = memberService.findOrCreateByFirebaseToken(decodedToken);
                SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthentication(member));
            } catch (FirebaseAuthException | RuntimeException e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase ID token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static class FirebaseAuthentication extends AbstractAuthenticationToken {

        private final Member member;

        FirebaseAuthentication(Member member) {
            super(List.of(new SimpleGrantedAuthority("ROLE_USER")));
            this.member = member;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return member;
        }
    }
}
