package org.jsp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id:}")
    private String firebaseProjectId;

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Check if already initialized to prevent duplicate initialization exceptions during hot-reloads
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder builder = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault());

                // Set project ID explicitly if provided in properties, otherwise try to get from credentials
                if (firebaseProjectId != null && !firebaseProjectId.trim().isEmpty()) {
                    builder.setProjectId(firebaseProjectId);
                    System.out.println("Firebase Project ID set from application.properties: " + firebaseProjectId);
                } else {
                    // Try to extract project ID from credentials if running on GCP
                    try {
                        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                        if (credentials instanceof com.google.auth.oauth2.ServiceAccountCredentials) {
                            String projectId = ((com.google.auth.oauth2.ServiceAccountCredentials) credentials).getProjectId();
                            if (projectId != null) {
                                builder.setProjectId(projectId);
                                System.out.println("Firebase Project ID extracted from service account: " + projectId);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Could not extract project ID from credentials: " + e.getMessage());
                    }
                }

                FirebaseApp.initializeApp(builder.build());
                System.out.println("Firebase Application has been successfully initialized on GCP!");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
/*
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Read the service account JSON from resources folder
        FirebaseOptions options;
        try (InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream()) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    // Uncomment and add if you are using the Realtime Database:
                    // .setDatabaseUrl("https://<YOUR-PROJECT-ID>-default-rtdb.firebaseio.com")
                    .build();
        }

        // Prevent re-initialization if the app reloads
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }*/
}