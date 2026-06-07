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

    public boolean deleteByEmail(String collection, String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(collection).whereEqualTo("email", email).get();
        QuerySnapshot querySnapshot = future.get();
        if (querySnapshot.isEmpty()) {
            return false; // No document found
        }
        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        doc.getReference().delete(); // Delete the document
        return true; // Return success status
    }

    public <T> T findByEmail(String collection, Class<T> clazz, String email) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        // Query documents where the `email` field equals the provided email value
        ApiFuture<QuerySnapshot> future = db.collection(collection).whereEqualTo("email", email).get();
        QuerySnapshot querySnapshot = future.get();
        // Return the first matching document converted to the requested class, or null if none found
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            T obj = doc.toObject(clazz);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    public <T> T findByProperty(String collection, Class<T> clazz, String property, String value) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(collection).whereEqualTo(property, value).get();
        QuerySnapshot querySnapshot = future.get();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            T obj = doc.toObject(clazz);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    public String addData(String collection, Object data) {
        // Use add() to create a new document with auto-generated ID
        Firestore db = FirestoreClient.getFirestore();
        String documentId = db.collection(collection).document().getId();
        db.collection(collection).document(documentId).set(data);
        System.out.println("New record added successfully to Firestore with ID: " + documentId);
        return documentId;
    }

    public String updateByEmail(String collection, String email, Object data) throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(collection).whereEqualTo("email", email).get();
        QuerySnapshot querySnapshot = future.get();
        if (querySnapshot.isEmpty()) {
            return null; // No document found to update
        }
        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        doc.getReference().set(data); // Update the document with new data
        return doc.getId(); // Return the document ID of the updated record
    }
}
