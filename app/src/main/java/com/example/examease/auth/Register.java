package com.example.examease.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.db.FirebaseHelper;
import com.example.examease.misc.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Register extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Assuming the layout is activity_register.xml

        // Initialize FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Bind the UI elements
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        confirmPasswordEditText = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar);

        // Set the register button listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Validate inputs
                if (validateInputs(email, password, confirmPassword)) {
                    progressBar.setVisibility(View.VISIBLE);

                    // Call registerUser from FirebaseHelper
                    firebaseHelper.registerUser(email, password, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registration successful, go to Home activity
                                Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Registration failed, show error
                                Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE); // Hide progress bar after registration attempt
                        }
                    });
                }
            }
        });
    }

    // Method to validate user input
    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return false;
        }

        return true;
    }

    // Method to navigate to Login page
    public void goToLogin(View v) {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }
}
