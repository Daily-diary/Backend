package com.likelion.daily_diary.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account.path:}")
    private String serviceAccountPath;

    @Value("${firebase.service-account.json:}")
    private String serviceAccountJson;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder();

        try (InputStream serviceAccount = resolveInputStream()) {
            optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
        }

        if (projectId != null && !projectId.isBlank()) {
            optionsBuilder.setProjectId(projectId);
        }

        return FirebaseApp.initializeApp(optionsBuilder.build());
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    private InputStream resolveInputStream() throws IOException {
        // 환경변수로 Base64 인코딩된 JSON이 있으면 우선 사용
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(serviceAccountJson.trim());
            return new ByteArrayInputStream(decoded);
        }

        if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
            throw new IllegalStateException("Firebase 설정이 없습니다. FIREBASE_SERVICE_ACCOUNT_JSON 또는 firebase.service-account.path를 설정하세요.");
        }

        if (serviceAccountPath.startsWith("classpath:")) {
            return new ClassPathResource(serviceAccountPath.substring("classpath:".length())).getInputStream();
        }

        return new FileInputStream(serviceAccountPath);
    }
}
