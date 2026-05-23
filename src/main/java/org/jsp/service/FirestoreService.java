package org.jsp.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

   /* public boolean saveData(String collection, String documentId, Object data) {
        // Access Firestore directly via the initialized Admin SDK
        Firestore db = FirestoreClient.getFirestore();
        db.collection(collection).document(documentId).set(data);
        System.out.println("Data saved successfully to Firestore!");
        return true;
    }*/

    public <T> List<T> fetchAll(String collection, Class<T> clazz) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(collection).get();
        QuerySnapshot querySnapshot = future.get();
        List<T> results = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            T obj = doc.toObject(clazz);
            if (obj != null) {
                results.add(obj);
            }
        }
        return results;
    }

    public String addData(String collection, Object data) {
        // Use add() to create a new document with auto-generated ID
        Firestore db = FirestoreClient.getFirestore();
        String documentId = db.collection(collection).document().getId();
        db.collection(collection).document(documentId).set(data);
        System.out.println("New record added successfully to Firestore with ID: " + documentId);
        return documentId;
    }
}
