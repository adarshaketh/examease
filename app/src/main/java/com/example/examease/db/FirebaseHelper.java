package com.example.examease.db;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore firestore;

    // Constructor to initialize FirebaseAuth and Firestore
    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    // Check if the user is currently logged in
    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getUserInfo(){
        return mAuth.getCurrentUser();
    }

    // Authentication methods

    // Login a user using email and password
    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    // Register a new user using email and password
    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    // Send a password reset email
    public void sendPasswordResetEmail(String email, Context context) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Password reset email sent successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Fetch exam details from Firestore using examId
    public void fetchExamDetails(String examId, OnCompleteListener<DocumentSnapshot> listener) {
        firestore.collection("exams").document(examId).get().addOnCompleteListener(listener);
    }

    // Update Firestore when the exam starts (record start timestamp)
    public void updateExamStart(String userEmail, String examId, long startTime, OnCompleteListener<Void> listener) {
        // Create the initial exam history structure
        Map<String, Object> examHistory = new HashMap<>();
        examHistory.put("examId", examId);
        examHistory.put("duration", 0); // Initial duration is 0 since the exam just started
        examHistory.put("score", 0);    // Initial score is 0 since no questions are attempted
        examHistory.put("noQnsAttempted", 0); // No questions attempted yet
        examHistory.put("start", startTime); // Store the start timestamp

        // Reference to the user's history document
        DocumentReference historyRef = firestore.collection("history").document(userEmail);

        // Try to update the document if it exists, else create it
        historyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // If the document exists, update the array field
                    historyRef.update("examIds", FieldValue.arrayUnion(examHistory))
                            .addOnCompleteListener(listener);
                } else {
                    // If the document does not exist, create a new document with the exam history
                    Map<String, Object> newHistory = new HashMap<>();
                    newHistory.put("examIds", Arrays.asList(examHistory)); // Create a new array with the exam entry
                    historyRef.set(newHistory)
                            .addOnCompleteListener(listener);
                }
            } else {
                // Handle the error if needed

            }
        });
    }


    // Update Firestore when the exam is completed
    public void updateExamCompletion(String userEmail, String examId, long duration, int score, int noQnsAttempted, OnCompleteListener<Void> listener) {
        firestore.collection("history").document(userEmail).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> examIds = (List<Map<String, Object>>) documentSnapshot.get("examIds");
                if (examIds != null) {
                    // Find the exam entry by examId
                    for (Map<String, Object> examHistory : examIds) {
                        if (examHistory.get("examId").equals(examId)) {
                            // Update the fields without touching the "start" timestamp
                            examHistory.put("duration", duration);
                            examHistory.put("score", score);
                            examHistory.put("noQnsAttempted", noQnsAttempted);
                            break;
                        }
                    }

                    // Update the document with the modified examIds array
                    firestore.collection("history").document(userEmail)
                            .update("examIds", examIds)
                            .addOnCompleteListener(listener); // Notify when the update is complete
                }
            } else {
                // Handle the case where the document doesn't exist
                listener.onComplete(null);  // You may want to handle this differently
            }
        }).addOnFailureListener(e -> {
            // Handle failure in fetching the document
            listener.onComplete(null);  // Notify listener of failure
        });
    }

}
