package com.example.examease.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class activity_edit_profile extends AppCompatActivity {

    // UI Components
    private TextInputEditText nameEditText, emailEditText;
    private Button saveButton;
    private TextView profileNameTextView;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // Ensure this matches your XML file name

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("ActivityEditProfile", "No authenticated user found");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();  // Exit the activity if no user is logged in
            return;
        }

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);
        profileNameTextView = findViewById(R.id.profileNameTextView);  // TextView below the profile picture

        // Initially disable the Save button
        saveButton.setEnabled(false);

        // Load current user data
        loadUserData();

        // Add listeners to detect changes in the EditText fields
        addTextChangeListeners();

        // Set click listener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();  // Save user data on click
            }
        });
    }

    /**
     * Loads the current user's data from Firebase Authentication and populates the UI fields.
     */
    private void loadUserData() {
        Log.d("ActivityEditProfile", "loadUserData() called");

        // Get user info from Firebase Auth
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        Log.d("ActivityEditProfile", "User info - Name: " + name + ", Email: " + email);

        // Populate the EditTexts with existing data
        nameEditText.setText(name);
        emailEditText.setText(email);

        // Set the name in the TextView below the profile picture
        profileNameTextView.setText(name);
    }

    /**
     * Adds TextWatchers to the EditText fields to detect changes and enable the Save button.
     */
    private void addTextChangeListeners() {
        // Add a TextWatcher to detect changes in the name or email fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the Save button if the fields have changed
                checkForChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        };

        nameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
    }

    /**
     * Checks if there are any changes in the user profile fields.
     * If there are changes, it enables the Save button.
     */
    private void checkForChanges() {
        // Get current text from EditTexts
        String currentName = nameEditText.getText().toString().trim();
        String currentEmail = emailEditText.getText().toString().trim();

        // Get current user info from Firebase Auth
        String originalName = currentUser.getDisplayName();
        String originalEmail = currentUser.getEmail();

        // Enable the Save button if there are changes in the name or email
        if (!currentName.equals(originalName) || !currentEmail.equals(originalEmail)) {
            saveButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
        }
    }

    /**
     * Saves the updated user profile to Firebase Authentication.
     */
    private void saveUserProfile() {
        Log.d("ActivityEditProfile", "saveUserProfile() called");

        // Retrieve user inputs
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();

        // Check if changes are needed for name or email
        boolean nameChanged = !newName.equals(currentUser.getDisplayName());
        boolean emailChanged = !newEmail.equals(currentUser.getEmail());

        // Update only if changes are detected
        if (nameChanged || emailChanged) {
            updateFirebaseAuthProfile(newName, newEmail, nameChanged, emailChanged);
        } else {
            Log.d("ActivityEditProfile", "No changes detected, skipping update.");
            Toast.makeText(activity_edit_profile.this, "No changes to save", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the Firebase Authentication profile for the user's name and email.
     *
     * @param newName  The new display name.
     * @param newEmail The new email.
     */
    private void updateFirebaseAuthProfile(String newName, String newEmail, boolean nameChanged, boolean emailChanged) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e("ActivityEditProfile", "No authenticated user found");
            return;
        }

        // Update display name if it has changed
        if (nameChanged) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ActivityEditProfile", "User profile updated with display name");
                    profileNameTextView.setText(newName); // Update the display name in the UI
                } else {
                    Log.e("ActivityEditProfile", "Failed to update display name", task.getException());
                }
            });
        }

        // Update email if it has changed
        if (emailChanged) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ActivityEditProfile", "User email address updated.");
                    Toast.makeText(activity_edit_profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ActivityEditProfile", "Failed to update email", task.getException());
                    Toast.makeText(activity_edit_profile.this, "Failed to update email. Re-authentication might be required.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("ActivityEditProfile", "Email update failed", e);
                Toast.makeText(activity_edit_profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

        // Disable the Save button after saving changes
        saveButton.setEnabled(false);
    }
}