package org.jsp.controller;

import org.jsp.model.JobApplication;
import org.jsp.service.JobApplicationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutionException;

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
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(@RequestPart JobApplication jobApplication, @RequestParam("resumeFile") MultipartFile resumeFile) {
        return jobApplicationService.save(jobApplication, resumeFile);
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@RequestPart JobApplication jobApplication, @RequestParam("resumeFile") MultipartFile resumeFile) {
        return jobApplicationService.update(jobApplication, resumeFile);
    }

    @DeleteMapping(value = "/delete/{email}")
    public ResponseEntity<?> delete(@PathVariable("email") String email) {
        return jobApplicationService.delete(email);
    }

    /**
     * Get all registrations
     * GET /api/job-portal/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllJobApplications() {
        return jobApplicationService.getAllJobApplications();
    }

    /**
     * Get a job application by email
     * GET /api/job-portal/search/by-email?email=someone@example.com
     */
    @GetMapping("/search/{email}")
    public ResponseEntity<?> getJobApplicationByEmail(@PathVariable("email") String email) {
        return jobApplicationService.findByEmailId(email);
    }

    @GetMapping("/searchCandidates")
    public ResponseEntity<?> searchCandidates(@RequestParam(required = false) String skills,
                                              @RequestParam(required = false) String qualification,
                                              @RequestParam(required = false) String location,
                                              @RequestParam(required = false) String noticePeriod,
                                              @RequestParam(required = false) String experience) {
        return jobApplicationService.searchCandidates(skills, qualification, location, noticePeriod, experience);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadResume(@PathVariable("fileName") String fileName) {
        return jobApplicationService.downloadResume(fileName);
    }

    @GetMapping("/validate-email")
    public boolean validateEmail(@RequestParam final String email) throws ExecutionException, InterruptedException {
        return jobApplicationService.validateEmail(email);
    }

    @GetMapping("/validate-phone")
    public boolean validatePhone(@RequestParam final String phone) throws ExecutionException, InterruptedException {
        return jobApplicationService.validatePhone(phone);
    }
}
