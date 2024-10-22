package com.example.examease.misc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {
    private ImageView[] stars = new ImageView[5];
    private int rating = 4; // Default rating is 4 stars
    private EditText etFeedback;
    private CheckBox cbAnonymous;
    private Button btnSubmit, btnBack;

    // Firebase instances
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        etFeedback = findViewById(R.id.etFeedback);
        cbAnonymous = findViewById(R.id.cbAnonymous);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnback);

        // Setting up star click listeners
        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(v -> setRating(index + 1));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackIntent = new Intent(Feedback.this, Home.class);
                startActivity(feedbackIntent);
                finish();
            }
        });
    }

    private void setRating(int starCount) {
        rating = starCount;
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.baseline_star_24);
            } else {
                stars[i].setImageResource(R.drawable.baseline_star_grey);
            }
        }
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText().toString();
        boolean isAnonymous = cbAnonymous.isChecked();

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Please write your feedback.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare feedback data
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("rating", rating);
        feedbackData.put("feedback", feedback);
        feedbackData.put("timestamp", System.currentTimeMillis());

        // If anonymous, save feedback in the 'anonymous' collection
        if (isAnonymous) {
            firestore.collection("feedbacks")
                    .document("anonymous")
                    .update("feedbacks", com.google.firebase.firestore.FieldValue.arrayUnion(feedbackData))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Feedback.this, "Anonymous feedback submitted successfully.", Toast.LENGTH_LONG).show();
                        finish(); // Close activity after submission
                    })
                    .addOnFailureListener(e -> Toast.makeText(Feedback.this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show());
        } else {
            // Get the logged-in user's email
            String userEmail = auth.getCurrentUser().getEmail();

            // Save feedback under the logged-in user's email
            firestore.collection("feedbacks")
                    .document(userEmail)
                    .update("feedbacks", com.google.firebase.firestore.FieldValue.arrayUnion(feedbackData))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Feedback.this, "Feedback submitted successfully.", Toast.LENGTH_LONG).show();
                        finish(); // Close activity after submission
                    })
                    .addOnFailureListener(e -> {
                        // If document does not exist, create a new one
                        firestore.collection("feedbacks")
                                .document(userEmail)
                                .set(new HashMap<String, Object>() {{
                                    put("feedbacks", com.google.firebase.firestore.FieldValue.arrayUnion(feedbackData));
                                }})
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Feedback.this, "Feedback submitted successfully.", Toast.LENGTH_LONG).show();
                                    finish();
                                })
                                .addOnFailureListener(e2 -> Toast.makeText(Feedback.this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show());
                    });
        }
    }
}
