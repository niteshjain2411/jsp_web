package org.jsp.controller;

import org.jsp.model.ServiceRequest;
import org.jsp.service.ServiceRequestService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/service-request")
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    private final ServiceRequestService applicationService;

    public ServiceRequestController(ServiceRequestService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveServiceRequest(@RequestPart ServiceRequest application, @RequestParam("file") MultipartFile file) {
        try {
            return applicationService.save(application, file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Malformed structure payload declaration data conversion framework mismatch.");
        }
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<?> updateServiceRequest(@PathVariable("id") String id, @RequestParam String status, @RequestParam String remarks) {
        try {
            return applicationService.update(id, status, remarks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Malformed structure payload declaration data conversion framework mismatch.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications() {
        return applicationService.fetchAllApplications();
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName) {
        return applicationService.downloadFile(fileName);
    }
}