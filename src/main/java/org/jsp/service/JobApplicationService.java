package org.jsp.service;

import org.jsp.model.JobApplication;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobApplicationService {

    private final FirestoreService firestoreService;

    public JobApplicationService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public ResponseEntity<?> save(JobApplication jobApplication) {
        if (jobApplication.getFullName() == null || jobApplication.getFullName().isEmpty()) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Full name is required"));
        }

        if (jobApplication.getEmail() == null || jobApplication.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Email is required"));
        }
        if (jobApplication.getPhone() == null || jobApplication.getPhone().isEmpty()) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Phone number is required"));
        }
        if (jobApplication.getQualification() == null || jobApplication.getQualification().isEmpty()) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Qualification details is required"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Job application saved successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getAllJobApplications() {
        try {
            List<JobApplication> jobApplications = firestoreService.fetchAll("job_applications", JobApplication.class);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", jobApplications.size());
            response.put("data", jobApplications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching job applications: " + e.getMessage()));
        }
    }
}