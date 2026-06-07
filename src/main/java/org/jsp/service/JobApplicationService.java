package org.jsp.service;

import org.jsp.model.JobApplication;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final String bucketName = "jain-sangh-pune-5c864.firebasestorage.app";

    private final FirestoreService firestoreService;
    private final FirebaseStorageService storageService;

    public JobApplicationService(FirestoreService firestoreService, FirebaseStorageService storageService) {
        this.firestoreService = firestoreService;
        this.storageService = storageService;
    }

    public ResponseEntity<?> update(JobApplication jobApplication, MultipartFile resumeFile) {
        try {
            // Upload resume to Firebase Storage
            final JobApplication existingApplication = firestoreService.findByEmail("job_seekers_data", JobApplication.class, jobApplication.getEmail());
            if (existingApplication == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpResponseUtil.createErrorResponse("Job application not found for email: " + jobApplication.getEmail()));
            }

            if (jobApplication.getQualification() == null || jobApplication.getQualification().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Qualification details is required"));
            }
            if (jobApplication.getExperience() == null || jobApplication.getExperience().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Experience details is required"));
            }

            if (resumeFile == null || resumeFile.isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Resume file is required"));
            }

            final Date now = new Date();
            if (jobApplication.getCreatedOn() == null) {
                jobApplication.setCreatedOn(now);
            }
            jobApplication.setLastUpdatedOn(now);

            // 2. Upload Resume to Firebase Storage
            // Target Bucket: gs://jain-sangh-pune-5c864.firebasestorage.app
            boolean isDeleted = storageService.deleteFile(existingApplication.getResumeFileName());
            if (isDeleted) {
                System.out.println("Existing resume deleted successfully: " + jobApplication.getResumeFileName());
            }
            jobApplication.setResumeFileName(jobApplication.getPhone() + "_" + resumeFile.getOriginalFilename());
            // 3. Save Record to Firestore collection: "job_seekers_data"
            final String documentId = firestoreService.updateByEmail("job_seekers_data", jobApplication.getEmail(), jobApplication);
            // 4. Send Success Response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Job application updated successfully");
            response.put("documentId", documentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in saveJobApplication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> save(JobApplication jobApplication, MultipartFile resumeFile) {
        try {
            // 1. Validation Logic
            if (jobApplication.getFullName() == null || jobApplication.getFullName().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Full name is required"));
            }
            if (jobApplication.getEmail() == null || jobApplication.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Email is required"));
            }
            if (jobApplication.getPhone() == null || jobApplication.getPhone().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Phone number is required"));
            }
            if (jobApplication.getCity() == null || jobApplication.getCity().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("City is required"));
            }

            if (jobApplication.getQualification() == null || jobApplication.getQualification().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Qualification details is required"));
            }
            if (jobApplication.getExperience() == null || jobApplication.getExperience().isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Experience details is required"));
            }

            if (resumeFile == null || resumeFile.isEmpty()) {
                return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse("Resume file is required"));
            }

            final Date now = new Date();
            if (jobApplication.getCreatedOn() == null) {
                jobApplication.setCreatedOn(now);
            }
            jobApplication.setLastUpdatedOn(now);

            // 2. Upload Resume to Firebase Storage
            // Target Bucket: gs://jain-sangh-pune-5c864.firebasestorage.app
            final String fileName = jobApplication.getPhone() + "_" + resumeFile.getOriginalFilename();
            storageService.uploadFile(resumeFile, fileName, "resumes/");
            jobApplication.setResumeFileName(fileName);

            // 3. Save Record to Firestore collection: "job_seekers_data"
            final String documentId = firestoreService.addData("job_seekers_data", jobApplication);

            // 4. Send Success Response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Job application saved successfully");
            response.put("documentId", documentId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in saveJobApplication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> delete(String email) {
        try {
            boolean deleted = firestoreService.deleteByEmail("job_seekers_data", email);
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Job application deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpResponseUtil.createErrorResponse("Job application not found for email: " + email));
            }
        } catch (Exception e) {
            System.err.println("Error in deleteJobApplication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> findByEmailId(String emailId) {
        try {
            JobApplication jobApplication = firestoreService.findByEmail("job_seekers_data", JobApplication.class, emailId);
            if (jobApplication == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpResponseUtil.createErrorResponse("No job application found for email: " + emailId));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", jobApplication);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAllJobApplications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllJobApplications() {
        try {
            List<JobApplication> jobApplications = firestoreService.fetchAll("job_seekers_data", JobApplication.class);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", jobApplications.size());
            response.put("data", jobApplications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching job applications: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> searchCandidates(String skills, String qualification, String location, String noticePeriod, String experience) {
        try {
            List<JobApplication> jobApplications = firestoreService.fetchAll("job_seekers_data", JobApplication.class);

            if (skills != null && !skills.isEmpty()) {
                jobApplications = jobApplications.stream()
                        .filter(app -> app.getSkillsSummary() != null && app.getSkillsSummary().toLowerCase().contains(skills.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (qualification != null && !qualification.isEmpty()) {
                jobApplications = jobApplications.stream()
                        .filter(app -> app.getQualification() != null && app.getQualification().toLowerCase().contains(qualification.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (location != null && !location.isEmpty()) {
                jobApplications = jobApplications.stream()
                        .filter(app -> app.getCity() != null && app.getCity().toLowerCase().contains(location.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (noticePeriod != null && !noticePeriod.isEmpty()) {
                jobApplications = jobApplications.stream()
                        .filter(app -> app.getNoticePeriod() != null && app.getNoticePeriod().toLowerCase().contains(noticePeriod.toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (experience != null && !experience.isEmpty()) {
                jobApplications = jobApplications.stream()
                        .filter(app -> app.getExperience() != null && app.getExperience().toLowerCase().contains(experience.toLowerCase()))
                        .collect(Collectors.toList());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", jobApplications.size());
            response.put("data", jobApplications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching job applications: " + e.getMessage()));
        }
    }

    public ResponseEntity<byte[]> downloadResume(String fileName) {
        try {
            byte[] data = storageService.downloadFile(fileName);
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Generic binary stream
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in downloadResume: " + e.getMessage());
            throw new RuntimeException("Error downloading resume: " + e.getMessage());
        }
    }
}