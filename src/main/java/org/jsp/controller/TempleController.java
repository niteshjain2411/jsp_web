package org.jsp.controller;

import org.jsp.service.TempleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/temple")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TempleController {

    private final TempleService templeService;

    public TempleController() {
        this.templeService = new TempleService();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTemplesData() {
        return templeService.getAllTemplesData();
    }
}
