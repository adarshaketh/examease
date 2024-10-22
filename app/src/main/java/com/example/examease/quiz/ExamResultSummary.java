package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.profile.activity_profile; // Import the profile activity
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class ExamResultSummary extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView attemptedText, notAttemptedText, totalQuestionsText, timeText, totalScoreText;
    private Button viewResultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result_summary);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views by ID
        attemptedText = findViewById(R.id.attempted_text);
        notAttemptedText = findViewById(R.id.not_attempted_text);
        totalQuestionsText = findViewById(R.id.total_questions_text);
        timeText = findViewById(R.id.time_text);
        totalScoreText = findViewById(R.id.total_score_text);
        viewResultsButton = findViewById(R.id.view_results_button);

        // Fetch the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            fetchRecentExamDetails(email);  // Fetch recent exam data for the current user
        } else {
            Toast.makeText(ExamResultSummary.this, "No user signed in.", Toast.LENGTH_SHORT).show();
        }

        // Set an OnClickListener for the View All Results button to navigate to profile page
        viewResultsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExamResultSummary.this, activity_profile.class);
            startActivity(intent);  // Start the profile activity
        });
    }

    private void fetchRecentExamDetails(String email) {
        // Fetch the most recent exam from the user's history
        db.collection("history").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> examIds = (List<Map<String, Object>>) documentSnapshot.get("examIds");
                        if (examIds != null && !examIds.isEmpty()) {
                            // Assuming the most recent exam is the last one in the list
                            Map<String, Object> recentExam = examIds.get(examIds.size() - 1);

                            String examId = (String) recentExam.get("examId");
                            int score = ((Long) recentExam.get("score")).intValue();
                            int noQnsAttempted = ((Long) recentExam.get("noQnsAttempted")).intValue();
                            int duration = ((Long) recentExam.get("duration")).intValue();

                            // Update UI with fetched values
                            attemptedText.setText(String.valueOf(noQnsAttempted));
                            timeText.setText(String.valueOf(duration));

                            // Now, use the examId to fetch total questions from the exams collection
                            fetchTotalQuestionsAndUpdateUI(examId, score, noQnsAttempted);
                        } else {
                            Toast.makeText(ExamResultSummary.this, "No recent exam found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExamResultSummary.this, "No history found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ExamResultSummary.this, "Failed to load exam data.", Toast.LENGTH_SHORT).show());
    }

    private void fetchTotalQuestionsAndUpdateUI(String examId, int score, int noQnsAttempted) {
        db.collection("exams").document(examId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<?> questions = (List<?>) documentSnapshot.get("questions");
                        int totalQuestionsCount = (questions != null) ? questions.size() : 0;

                        // Update the UI with total questions and calculated not attempted questions
                        totalQuestionsText.setText(String.valueOf(totalQuestionsCount));

                        int notAttemptedQuestions = totalQuestionsCount - noQnsAttempted;
                        notAttemptedText.setText(String.valueOf(notAttemptedQuestions));

                        // Update total score in the format "score / totalQuestions"
                        totalScoreText.setText("Total Score: " + score + " / " + documentSnapshot.get("totalMarks"));
                    } else {
                        Toast.makeText(ExamResultSummary.this, "Exam data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ExamResultSummary.this, "Failed to load exam details.", Toast.LENGTH_SHORT).show());
    }
}
