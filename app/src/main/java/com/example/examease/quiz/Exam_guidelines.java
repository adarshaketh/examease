package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.db.FirebaseHelper;
import com.example.examease.helpers.Functions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class Exam_guidelines extends AppCompatActivity {
    private TextView examTitleTextView, examDurationTextView;
    private MaterialButton backButton, startButton;

    // Declare FirebaseHelper and necessary variables
    private FirebaseHelper firebaseHelper;
    private FirebaseUser firebaseUserInfo;
    private String examId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_guidelines);

        // Initialize FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Initialize views
        examTitleTextView = findViewById(R.id.tv_exam_title);
        examDurationTextView = findViewById(R.id.tv_exam_duration);
        backButton = findViewById(R.id.btn_back);
        startButton = findViewById(R.id.btn_start);

        //get examId from intent
        examId = getIntent().getStringExtra("examid");

        // Set up Back button
        backButton.setOnClickListener(v -> finish()); // Close the activity on back press

        //fetch user info
        firebaseUserInfo = firebaseHelper.getUserInfo();
        userEmail = firebaseUserInfo.getEmail();

        // Fetch exam details from Firestore
        fetchExamDetails();

        // Set up Start button
        startButton.setOnClickListener(v -> startExam());
    }

    // Fetch exam details from Firestore using FirebaseHelper
    private void fetchExamDetails() {
        firebaseHelper.fetchExamDetails(examId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract exam details from Firestore document
                    String title = document.getString("title");

                    long duration = document.getLong("duration");

                    // Get the number of questions if the 'questions' field is a List
                    List<?> questions = (List<?>) document.get("questions");
                    int numberOfQuestions = (questions != null) ? questions.size() : 0;

                    // Update UI with fetched details
                    examTitleTextView.setText(Functions.makeBold(title));
                    examDurationTextView.setText(Functions.makeBold("Duration: " + duration + " seconds"));
                } else {
                    // Handle case where the document doesn't exist
                    Toast.makeText(Exam_guidelines.this, "Exam not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the failure of fetching data
                Toast.makeText(Exam_guidelines.this, "Error loading exam details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the Start button click to update Firestore and start the exam
    private void startExam() {
        // Record the start time of the exam
        long startTime = System.currentTimeMillis();

        // Use FirebaseHelper to update Firestore with the exam start timestamp
        firebaseHelper.updateExamStart(userEmail, examId, startTime, task -> {
            if (task.isSuccessful()) {
                // Successfully updated Firestore, start the ExamAttempt activity
                Intent startExamIntent = new Intent(Exam_guidelines.this, ExamAttempt.class);
                startExamIntent.putExtra("examId", examId); // Pass the examId to ExamAttempt activity
                startActivity(startExamIntent);
                finish(); // Optionally close the current activity
            } else {
                // Handle error in updating Firestore
                Toast.makeText(Exam_guidelines.this, "Error starting exam", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
