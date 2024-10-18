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

    private TextInputEditText emailEditText;  // Make sure this is TextInputEditText
    private MaterialButton sendLinkButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Correctly reference TextInputEditText
        emailEditText = findViewById(R.id.email_input);  // Correct view ID for TextInputEditText
        sendLinkButton = findViewById(R.id.btnSendLink);

        sendLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

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


