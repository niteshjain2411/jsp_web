package org.jsp.controller;

import org.jsp.model.ContactFormRequest;
import org.jsp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/contact")
@CrossOrigin(origins = "*") // Adjust CORS permissions to match your deployment domain
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> submitContactForm(@Valid @RequestBody ContactFormRequest request) {
        try {
            emailService.sendContactInquiry(request);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Enquiry sent successfully."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to send email inquiry."));
        }
    }
}
