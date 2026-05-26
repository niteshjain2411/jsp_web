package org.jsp.service;

import org.jsp.model.VolunteerRegistrationData;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VolunteerRegistrationService {

    private final FirestoreService firestoreService;

    public VolunteerRegistrationService() {
        this.firestoreService = new FirestoreService();
    }

    public ResponseEntity<?> save(VolunteerRegistrationData registration) {
        try {
            // Validate required fields
            if (registration.getFullName() == null || registration.getFullName().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Full name is required"));
            }

            if (registration.getEmail() == null || registration.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Email is required"));
            }
            if (registration.getPhone() == null || registration.getPhone().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Phone number is required"));
            }
            if (registration.getAge() != null && (registration.getAge() <= 0 || registration.getAge() > 120)) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Please enter a valid age (1-120)"));
            }

            final String documentId = firestoreService.addData("volunteer_data", registration);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration saved successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in saveRegistration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllRegistrations() {
        try {
            List<VolunteerRegistrationData> registrations = firestoreService.fetchAll("volunteer_data", VolunteerRegistrationData.class);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", registrations.size());
            response.put("data", registrations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching registrations: " + e.getMessage()));
        }
    }
}


