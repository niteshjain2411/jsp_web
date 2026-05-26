package org.jsp.controller;

import org.jsp.service.DharamshalaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dharamshala")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DharamshalaController {
    private final DharamshalaService dharamshalaService;

    public DharamshalaController() {
        this.dharamshalaService = new DharamshalaService();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDharamshalaData() {
        return dharamshalaService.getAllDharamshalaData();
    }
}
