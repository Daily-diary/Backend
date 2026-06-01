package com.likelion.daily_diary.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account.path:}")
    private String serviceAccountPath;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
            throw new IllegalStateException("Missing firebase.service-account.path");
        }

        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder();

        try (InputStream serviceAccount = resolveInputStream(serviceAccountPath)) {
            optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
        }

        if (projectId != null && !projectId.isBlank()) {
            optionsBuilder.setProjectId(projectId);
        }

        return FirebaseApp.initializeApp(optionsBuilder.build());
    }

    private InputStream resolveInputStream(String path) throws IOException {
        if (path.startsWith("classpath:")) {
            String classpathLocation = path.substring("classpath:".length());
            return new ClassPathResource(classpathLocation).getInputStream();
        }

        return new FileInputStream(path);
    }
}
