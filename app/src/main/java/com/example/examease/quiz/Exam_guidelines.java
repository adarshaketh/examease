package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.Objects;

public class Exam_guidelines extends AppCompatActivity {
    private TextView examTitleTextView, examDurationTextView, noQnsTextView, greetingTextView;
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
        greetingTextView = findViewById(R.id.tv_username);
        examTitleTextView = findViewById(R.id.tv_exam_title);
        examDurationTextView = findViewById(R.id.tv_exam_duration);
        noQnsTextView = findViewById(R.id.tv_total_questions);
        backButton = findViewById(R.id.btn_back);
        startButton = findViewById(R.id.btn_start);

        //get examId from intent
        examId = getIntent().getStringExtra("examid");

        // Set up Back button
        backButton.setOnClickListener(v -> finish()); // Close the activity on back press

        //fetch user info
        firebaseUserInfo = firebaseHelper.getUserInfo();
        greetingTextView.setText(Functions.makeBold("Hello, "+ ((!Objects.equals(firebaseUserInfo.getDisplayName(), "")) ? firebaseUserInfo.getDisplayName()  : "User")));
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
                    noQnsTextView.setText(Functions.makeBold("Total Questions: "+ numberOfQuestions));
                    showDuration(examDurationTextView,duration);
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

    private void showDuration(TextView view, long durationSec) {
        if (durationSec > 59) {
            // Calculate minutes and remaining seconds
            int durationMin = (int) (durationSec / 60);
            long remainingSec = durationSec % 60;  // Get remaining seconds
            if(remainingSec==0){
                view.setText(Functions.makeBold("Duration: " + durationMin + " Min "));
                return;
            }
            // Set the TextView with the formatted string
            view.setText(Functions.makeBold("Duration: " + durationMin + " Min " + remainingSec + " Sec"));
        } else {
            // If duration is less than 60 seconds, just show seconds
            view.setText(Functions.makeBold("Duration: " + durationSec + " Sec"));
        }
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
