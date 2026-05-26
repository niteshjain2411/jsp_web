package org.jsp.service;

import org.jsp.model.Dharamshala;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DharamshalaService {

    private final FirestoreService firestoreService;

    public DharamshalaService() {
        this.firestoreService = new FirestoreService();
    }

    public ResponseEntity<?> getAllDharamshalaData() {
        try {
            final List<Dharamshala> dharamshalas = firestoreService.fetchAll("dharamshala_data", Dharamshala.class);
            dharamshalas.sort((d1, d2) -> d1.getArea().compareToIgnoreCase(d2.getArea()));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dharamshalas.size());
            response.put("data", dharamshalas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching Dharamshala data: " + e.getMessage()));
        }
    }
}
