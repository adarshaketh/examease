package com.example.examease.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class activity_edit_profile extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, mobileNumberEditText;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);  // Ensure this matches your XML file name

        // Initialize Firebase components
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load existing user data
        loadUserData();

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
            }
        });
    }

    private void loadUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);

                    // Set data to EditTexts
                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    mobileNumberEditText.setText(mobileNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity_edit_profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobileNumber = mobileNumberEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to Firebase
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("email", email);
        userUpdates.put("mobileNumber", mobileNumber);

        databaseReference.updateChildren(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity_edit_profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity_edit_profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
