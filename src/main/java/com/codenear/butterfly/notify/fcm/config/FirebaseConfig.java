package com.codenear.butterfly.notify.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    private static final String FIREBASE_CONFIG_PATH = "firebase/firebase-service-account.json";

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials credentials = loadGoogleCredentials();
        FirebaseOptions options = createFirebaseOptions(credentials);
        FirebaseApp app = createFirebaseApp(options);

        return FirebaseMessaging.getInstance(app);
    }

    private GoogleCredentials loadGoogleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream());
    }

    private FirebaseOptions createFirebaseOptions(GoogleCredentials credentials) {
        return FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
    }

    private FirebaseApp createFirebaseApp(FirebaseOptions options) {
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
