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

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.db.FirebaseHelper;
import com.example.examease.misc.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Bind the UI elements
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        // Set the login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate email and password
                if (validateInputs(email, password)) {
                    // Call loginUser from FirebaseHelper
                    firebaseHelper.loginUser(email, password, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Login successful, go to Home activity
                                Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Login failed, show error
                                Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    // Method to validate user input
    private boolean validateInputs(String email, String password) {
        // Validate email format
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

        // Check for spaces
        if (password.contains(" ")) {
            passwordEditText.setError("Password must not contain any spaces.");
            return false;
        }

        // All validations passed
        return true;
    }

    public void goToRegister(View v){
        Intent login = new Intent(Login.this, Register.class);
        startActivity(login);
        finish();
    }

    public void goToForgotPass(View v){
        Intent login = new Intent(Login.this, ForgotPassword.class);
        startActivity(login);
    }
}
