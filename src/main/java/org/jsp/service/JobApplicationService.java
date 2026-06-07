package org.jsp.service;

import org.jsp.model.JobApplication;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@Service
public class JobApplicationService {

    private static final String COLLECTION_NAME = "job_seekers_data";
    private final FirestoreService firestoreService;
    private final FirebaseStorageService storageService;

    public JobApplicationService(FirestoreService firestoreService, FirebaseStorageService storageService) {
        this.firestoreService = firestoreService;
        this.storageService = storageService;
    }

    public ResponseEntity<?> save(JobApplication app, MultipartFile file) {
        try {
            // Validate required fields
            this.validateRequiredFields(app, file, true);
            // Handle timestamps
            this.updateTimestamps(app);
            // Upload resume
            final String fileName = app.getPhone() + "_" + file.getOriginalFilename();
            storageService.uploadFile(file, fileName, "resumes/");
            app.setResumeFileName(fileName);

            // Save to Database
            final String docId = firestoreService.addData(COLLECTION_NAME, app);
            return ResponseEntity.ok(Map.of("success", true, "message", "Job application saved successfully", "documentId", docId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> update(JobApplication app, MultipartFile file) {
        try {
            // Check if record exists
            final JobApplication existing = firestoreService.findByEmail(COLLECTION_NAME, JobApplication.class, app.getEmail());
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpResponseUtil.createErrorResponse("Job application not found for email: " + app.getEmail()));
            }

            // Validate fields needed for updates
            this.validateRequiredFields(app, file, false);
            this.updateTimestamps(app);

            // Swap files in Storage (Delete old -> Upload new)
            if (existing.getResumeFileName() != null) {
                storageService.deleteFile(existing.getResumeFileName());
            }
            final String newFileName = app.getPhone() + "_" + file.getOriginalFilename();
            storageService.uploadFile(file, newFileName, "resumes/"); // Bug fixed: Upload was missing!
            app.setResumeFileName(newFileName);

            // Update Database
            final String docId = firestoreService.updateByEmail(COLLECTION_NAME, app.getEmail(), app);
            return ResponseEntity.ok(Map.of("success", true, "message", "Job application updated successfully", "documentId", docId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> delete(String email) {
        try {
            if (firestoreService.deleteByEmail(COLLECTION_NAME, email)) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Job application deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpResponseUtil.createErrorResponse("Job application not found for email: " + email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> findByEmailId(String emailId) {
        try {
            final JobApplication app = firestoreService.findByEmail(COLLECTION_NAME, JobApplication.class, emailId);
            if (app == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpResponseUtil.createErrorResponse("No job application found for email: " + emailId));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", app));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllJobApplications() {
        try {
            final List<JobApplication> apps = firestoreService.fetchAll(COLLECTION_NAME, JobApplication.class);
            return ResponseEntity.ok(Map.of("success", true, "count", apps.size(), "data", apps));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching applications: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> searchCandidates(String skills, String qualification, String location, String noticePeriod, String experience) {
        try {
            final List<JobApplication> filtered = firestoreService.fetchAll(COLLECTION_NAME, JobApplication.class).stream()
                    .filter(app -> matchesFilter(app.getSkillsSummary(), skills))
                    .filter(app -> matchesFilter(app.getQualification(), qualification))
                    .filter(app -> matchesFilter(app.getCity(), location))
                    .filter(app -> matchesFilter(app.getNoticePeriod(), noticePeriod))
                    .filter(app -> matchesFilter(app.getExperience(), experience))
                    .sorted(Comparator.comparing(JobApplication::getLastUpdatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();

            return ResponseEntity.ok(Map.of("success", true, "count", filtered.size(), "data", filtered));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error searching candidates: " + e.getMessage()));
        }
    }

    public ResponseEntity<byte[]> downloadResume(String fileName) {
        try {
            final byte[] data = storageService.downloadFile(fileName);
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading resume: " + e.getMessage());
        }
    }

    public boolean validateEmail(String email) throws ExecutionException, InterruptedException {
        final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        boolean valid = Pattern.compile(emailRegex).matcher(email).matches();
        final JobApplication app = firestoreService.findByProperty(COLLECTION_NAME, JobApplication.class, "email", email);
        if (app != null) {
            // If email exists, it's only valid for updates, not for new entries
            valid = false;
        }
        return valid;
    }

    public boolean validatePhone(String phone) throws ExecutionException, InterruptedException {
        final String phoneRegex = "^[0-9]{10}$";
        boolean valid = Pattern.compile(phoneRegex).matcher(phone).matches();
        final JobApplication app = firestoreService.findByProperty(COLLECTION_NAME, JobApplication.class, "phone", phone);
        if (app != null) {
            // If phone exists, it's only valid for updates, not for new entries
            valid = false;
        }
        return valid;
    }
    // --- Private Helper Utilities ---

    private void validateRequiredFields(JobApplication app, MultipartFile file, boolean isNewRegistration) {
        if (isNewRegistration) {
            checkBlank(app.getFullName(), "Full name is required");
            checkBlank(app.getEmail(), "Email is required");
            checkBlank(app.getPhone(), "Phone number is required");
            checkBlank(app.getCity(), "City is required");
        }
        checkBlank(app.getQualification(), "Qualification details is required");
        checkBlank(app.getExperience(), "Experience details is required");

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }
    }

    private void checkBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void updateTimestamps(JobApplication app) {
        final Date now = new Date();
        if (app.getCreatedOn() == null) {
            app.setCreatedOn(now);
        }
        app.setLastUpdatedOn(now);
    }

    private boolean matchesFilter(String fieldValue, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return true;
        }
        return fieldValue != null && fieldValue.toLowerCase().contains(searchTerm.toLowerCase());
    }
}