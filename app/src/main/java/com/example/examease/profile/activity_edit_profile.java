package com.example.examease.profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class activity_edit_profile extends AppCompatActivity {

    // UI Components
    private TextInputEditText nameEditText, emailEditText, mobileNumberEditText;
    private Button saveButton;
    private TextView profileNameTextView;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // Ensure this matches your XML file name

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Fetch the currently logged-in user
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("ActivityEditProfile", "No authenticated user found");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();  // Exit the activity if no user is logged in
            return;
        } else {
            // Get user info
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();

            // Log user info for debugging
            Log.d("ActivityEditProfile", "User ID: " + userId);
            Log.d("ActivityEditProfile", "Email: " + email);
            Log.d("ActivityEditProfile", "Display Name: " + displayName);
        }

        // Reference to the current user's node in Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        saveButton = findViewById(R.id.saveButton);
        profileNameTextView = findViewById(R.id.profileNameTextView);  // TextView below the profile picture

        // Load existing user data into the UI
        loadUserData();  // This will attempt to load user data, or initialize empty inputs if not found

        // Set click listener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();  // Save user data on click
            }
        });
    }

    /**
     * Loads the current user's data from Firebase Realtime Database and populates the UI fields.
     * If the data does not exist yet (first-time user), it initializes the input fields as empty.
     */
    private void loadUserData() {
        Log.d("ActivityEditProfile", "loadUserData() called");

        // Check for network connectivity
        if (!isNetworkAvailable()) {
            Log.e("ActivityEditProfile", "No internet connection");
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve user data once from the database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ActivityEditProfile", "onDataChange() called");

                if (snapshot.exists()) {
                    // User data already exists, load it into UI
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);

                    Log.d("ActivityEditProfile", "Retrieved Data - Name: " + name + ", Email: " + email + ", Mobile: " + mobileNumber);

                    // Populate the EditTexts with existing data
                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    mobileNumberEditText.setText(mobileNumber);

                    // Set the name in the TextView below the profile picture
                    profileNameTextView.setText(name);
                } else {
                    // No data exists yet, set fields as empty for the first time
                    Log.d("ActivityEditProfile", "No existing user data, setting empty inputs for first-time save.");
                    nameEditText.setText("");  // First-time save, so leave it empty
                    emailEditText.setText(currentUser.getEmail());  // Pre-fill email from Firebase Auth if available
                    mobileNumberEditText.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ActivityEditProfile", "Failed to load data", error.toException());
                Toast.makeText(activity_edit_profile.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the updated user profile to Firebase Realtime Database and Firebase Authentication.
     */
    private void saveUserProfile() {
        Log.d("ActivityEditProfile", "saveUserProfile() called");

        // Check for network connectivity
        if (!isNetworkAvailable()) {
            Log.e("ActivityEditProfile", "No internet connection");
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve and trim user inputs
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();

        Log.d("ActivityEditProfile", "Input - Name: " + name + ", Email: " + email + ", Mobile: " + mobileNumber);

        // Validate inputs
        if (!validateInputs(name, email, mobileNumber)) {
            Log.e("ActivityEditProfile", "Input validation failed");
            return;  // Exit if validation fails
        }

        // Prepare the data to be updated in Firebase Realtime Database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("email", email);
        userUpdates.put("mobileNumber", mobileNumber);

        Log.d("ActivityEditProfile", "Writing new data to Firebase Realtime Database for the first time");

        // Write the user data in the database
        databaseReference.setValue(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ActivityEditProfile", "Firebase Realtime Database updated successfully");
                Toast.makeText(activity_edit_profile.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ActivityEditProfile", "Failed to save Firebase Realtime Database", task.getException());
                Toast.makeText(activity_edit_profile.this, "Failed to save profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("ActivityEditProfile", "Error saving Firebase Realtime Database", e);
            Toast.makeText(activity_edit_profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Validates the user inputs for name, email, and mobile number.
     *
     * @param name         The entered name.
     * @param email        The entered email.
     * @param mobileNumber The entered mobile number.
     * @return True if all inputs are valid; False otherwise.
     */
    private boolean validateInputs(String name, String email, String mobileNumber) {
        boolean isValid = true;

        // Validate Name
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            isValid = false;
        }

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            isValid = false;
        }

        // Validate Mobile Number
        if (TextUtils.isEmpty(mobileNumber)) {
            mobileNumberEditText.setError("Mobile number is required");
            mobileNumberEditText.requestFocus();
            isValid = false;
        } else if (!TextUtils.isDigitsOnly(mobileNumber) || mobileNumber.length() < 10) {
            mobileNumberEditText.setError("Invalid mobile number");
            mobileNumberEditText.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks whether the device has an active internet connection.
     *
     * @return True if connected; False otherwise.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) { // Added null check for safety
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            Log.d("ActivityEditProfile", "Network connectivity: " + isConnected);
            return isConnected;
        }
        Log.e("ActivityEditProfile", "ConnectivityManager is null");
        return false;
    }
}
