package org.jsp.controller;

import org.jsp.model.JobApplication;
import org.jsp.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-portal")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    /**
     * Save a new job application
     * POST /api/job-portal/save
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveRegistration(@RequestBody JobApplication jobApplication) {
        return jobApplicationService.save(jobApplication);
    }

    /**
     * Get all registrations
     * GET /api/job-portal/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllJobApplications() {
        return jobApplicationService.getAllJobApplications();
    }
}
