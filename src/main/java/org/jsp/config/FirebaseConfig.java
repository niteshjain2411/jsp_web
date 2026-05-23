package org.jsp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initialFirebaseApp() throws IOException {
        // Safe check: If Cloud Run reuses an instance context where Firebase is already alive, use it.
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        // On Cloud Run, GoogleCredentials.getApplicationDefault() automatically gathers
        // permissions from the Cloud Run Service Account. No service account JSON file needed!
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build();

        return FirebaseApp.initializeApp(options);
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

    @PostConstruct
    public void init() {
        try {
            // Reads the raw JSON string directly from the Cloud Run environment variable
            String jsonKey = System.getenv("FIREBASE_CONFIG_JSON");

            if (jsonKey != null) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(jsonKey.getBytes())))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}