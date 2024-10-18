package com.example.examease.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private MaterialButton sendLinkButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get references to views
        emailEditText = findViewById(R.id.tilEmail);
        sendLinkButton = findViewById(R.id.btnSendLink);

        // Set onClickListener for the "Send Link" button
        sendLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString().trim();

        // Check if email is valid
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send password reset email using Firebase Auth
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
