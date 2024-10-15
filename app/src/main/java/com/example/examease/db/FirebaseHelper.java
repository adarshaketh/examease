package com.example.examease.db;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Authentication methods
    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    // Method to send a password reset email
    public void sendPasswordResetEmail(String email, Context context) {
        // Ensure the email is not empty
        if (email == null || email.isEmpty()) {
            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
            return;
        }

        // Send password reset email
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Notify user that the reset email was sent
                Toast.makeText(context, "Password reset email sent successfully.", Toast.LENGTH_LONG).show();
            } else {
                // Notify user of the failure
                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Fetch quizzes
    public void fetchQuizzes(ValueEventListener listener) {
        databaseReference.child("quizzes").addListenerForSingleValueEvent(listener);
    }
}
