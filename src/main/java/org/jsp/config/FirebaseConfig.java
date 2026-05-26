package org.jsp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id:}")
    private String firebaseProjectId;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault()) // No JSON path needed!
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Bean
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