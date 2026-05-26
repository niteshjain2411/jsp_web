package org.jsp.service;

import org.jsp.model.TempleData;
import org.jsp.util.HttpResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TempleService {

    private final FirestoreService firestoreService;

    public TempleService() {
        this.firestoreService = new FirestoreService();
    }

    public ResponseEntity<?> getAllTemplesData() {
        try {
            final List<TempleData> temples = firestoreService.fetchAll("temple_data", TempleData.class);
            temples.sort((t1, t2) -> t1.getArea().compareToIgnoreCase(t2.getArea()));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", temples.size());
            response.put("data", temples);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HttpResponseUtil.createErrorResponse("Error fetching temples data: " + e.getMessage()));
        }
    }
}
