package com.example.examease.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    // Regex pattern for password validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +                 // Start of string
                    "(?=.*[0-9])" +       // At least one digit
                    "(?=.*[a-z])" +       // At least one lowercase letter
                    "(?=.*[A-Z])" +       // At least one uppercase letter
                    "(?=.*[@#$%^&+=!])" + // At least one special character
                    "(?=\\S+$)" +         // No white spaces
                    ".{6,}" +             // At least 6 characters
                    "$"                   // End of string
    );

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
        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        }

        if (email.matches(".*\\s+.*")) {
            emailEditText.setError("Email cannot contain spaces.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format. Please enter a valid email (e.g., example@example.com).");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        }

        // Validate password length
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long.");
            return false;
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            passwordEditText.setError("Password must contain at least 1 digit.");
            return false;
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            passwordEditText.setError("Password must contain at least 1 uppercase letter.");
            return false;
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            passwordEditText.setError("Password must contain at least 1 lowercase letter.");
            return false;
        }

        // Check for at least one special character
        if (!password.matches(".*[@#$%^&+=!].*")) {
            passwordEditText.setError("Password must contain at least 1 special character (e.g., @, #, $, etc.).");
            return false;
        }

        // Check for spaces in password
        if (password.contains(" ")) {
            passwordEditText.setError("Password must not contain any spaces.");
            return false;
        }

        // Confirm password
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
