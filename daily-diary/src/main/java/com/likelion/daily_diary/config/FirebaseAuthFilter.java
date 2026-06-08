package com.likelion.daily_diary.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.likelion.daily_diary.domain.user.entity.User;
import com.likelion.daily_diary.domain.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final FirebaseAuth firebaseAuth;
    private final UserService userService;

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
                User user = userService.findOrCreateByFirebaseToken(decodedToken);
                SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthentication(user));
            } catch (FirebaseAuthException | RuntimeException e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase ID token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static class FirebaseAuthentication extends AbstractAuthenticationToken {

        private final User user;

        FirebaseAuthentication(User user) {
            super(List.of(new SimpleGrantedAuthority("ROLE_USER")));
            this.user = user;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return user;
        }
    }
}
