package org.jsp.controller;

import org.jsp.model.VolunteerRegistrationData;
import org.jsp.service.VolunteerRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VolunteerRegistrationController {

    private final VolunteerRegistrationService registrationService;

    public VolunteerRegistrationController() {
        this.registrationService = new VolunteerRegistrationService();
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRegistration(@RequestBody VolunteerRegistrationData registration) {
        return registrationService.save(registration);
    }

    /**
     * Get all registrations
     * GET /api/registration/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }
}
