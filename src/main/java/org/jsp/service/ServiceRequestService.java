package org.jsp.service;

import org.jsp.model.ServiceRequest;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ServiceRequestService {

    private static final String COLLECTION_NAME = "service_request_data";
    private final FirestoreService firestoreService;
    private final FirebaseStorageService storageService;

    public ServiceRequestService(FirestoreService firestoreService, FirebaseStorageService storageService) {
        this.firestoreService = firestoreService;
        this.storageService = storageService;
    }

    public ResponseEntity<?> save(final ServiceRequest request, final MultipartFile file) {
        try {
            // Validate application specifications
            this.validateRequiredFields(request, file);
            // Handle entity update metadata timestamps
            this.updateTimestamps(request);
            request.setStatus("Pending");
            // Safe reference string assembly for standard attachments pipeline
            final String fileName = request.getPhone() + "_" + file.getOriginalFilename();
            storageService.uploadFile(file, fileName, "service_request_documents/");
            request.setDocumentFileName(fileName);
            // Persist document parameters payload structure to Cloud Firestore target
            final var docId = firestoreService.addData(COLLECTION_NAME, request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Job application saved successfully", "documentId", docId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(HttpResponseUtil.createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Server error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> update(final String id, final String status, final String remarks) {
        try {
            ServiceRequest existingRequest = firestoreService.findByProperty(COLLECTION_NAME, ServiceRequest.class, "id", id);
            if (existingRequest == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpResponseUtil.createErrorResponse("Application not found for ID: " + id));
            }

           /* Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("remarks", remarks);
            data.put("lastUpdatedOn", new Date());
            firestoreService.updateData(COLLECTION_NAME, id, data);*/

            existingRequest.setStatus(status);
            existingRequest.setRemarks(remarks);
            this.updateTimestamps(existingRequest);
            firestoreService.updateData(COLLECTION_NAME, id, existingRequest);
            return ResponseEntity.ok(Map.of("success", true, "message", "Application updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error updating application: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> fetchAllApplications() {
        try {
            List<ServiceRequest> applications = firestoreService.fetchAll(COLLECTION_NAME, ServiceRequest.class);
            return ResponseEntity.ok(Map.of("success", true, "count", applications.size(), "data", applications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching applications: " + e.getMessage()));
        }
    }

    public ResponseEntity<byte[]> downloadFile(String fileName) {
        try {
            final byte[] data = storageService.downloadFile("service_request_documents/", fileName);
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file: " + e.getMessage());
        }
    }

    // --- Private Helper Utilities ---
    private void validateRequiredFields(ServiceRequest app, MultipartFile file) {
        checkBlank(app.getFullName(), "Full Name is required");
        checkBlank(app.getDob(), "Date of Birth is required");
        checkBlank(app.getGender(), "Gender declaration is required");
        checkBlank(app.getFatherName(), "Father/Husband name is required");
        checkBlank(app.getPhone(), "Primary contact phone reference is required");
        checkBlank(app.getAddress(), "Operational address declaration parameter is missing");
        checkBlank(app.getOccupation(), "Occupation parameter details are required");
        checkBlank(app.getAppliedBefore(), "Prior application declarations response metric is required");
        checkBlank(app.getHelpWanted(), "Targeted support descriptions are required");
        checkBlank(app.getAfterHelpPlan(), "Future plan specification criteria is required");

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Payload validation failure: verification papers package stream is empty");
        }
    }

    private void checkBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void updateTimestamps(ServiceRequest app) {
        final Date now = new Date();
        if (app.getCreatedOn() == null) {
            app.setCreatedOn(now);
        }
        app.setLastUpdatedOn(now);
    }
}