package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.examease.R;
import com.example.examease.adapter.QuestionPagerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExamAttempt extends AppCompatActivity {

    private ViewPager2 viewPagerQuestions;
    private Button btnNext, btnFlag;
    private ImageButton btnBack, btnGrid;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 3600000; // 1 hour
    private TextView timerText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String examId;
    private QuestionPagerAdapter adapter;
    private int totalQuestions;
    private Set<Integer> flaggedQuestions = new HashSet<>(); // To track flagged questions
    private AlertDialog dialog;  // Declare dialog at the class level to manage its lifecycle


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_attempt);

        // Initialize views
        viewPagerQuestions = findViewById(R.id.viewPagerQuestions);
        btnNext = findViewById(R.id.btnNext);
        btnFlag = findViewById(R.id.btnFlag);
        btnBack = findViewById(R.id.btnBack);
        btnGrid = findViewById(R.id.btnGrid);
        timerText = findViewById(R.id.tvTimer);

        // Get examId from intent
        examId = getIntent().getStringExtra("examId");

        // Load questions from Firestore and set up ViewPager
        loadQuestionsFromFirestore();

        // Back button click listener
        btnBack.setOnClickListener(v -> showSubmissionConfirmationDialog());

        // Grid button click listener to open a dialog
        btnGrid.setOnClickListener(v -> showGridDialog());

        // Next button click listener
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                viewPagerQuestions.setCurrentItem(currentQuestionIndex);
                updateButtonState();
            } else {
                showSubmissionConfirmationDialog();
            }
        });

        // Flag button click listener
        btnFlag.setOnClickListener(v -> toggleFlagQuestion());

        // Start timer
        startTimer();

        // Handle back press action
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showSubmissionConfirmationDialog();
            }
        });
    }

    // Load exam questions from Firestore
    private void loadQuestionsFromFirestore() {
        db.collection("exams").document(examId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> questions = (List<Map<String, Object>>) documentSnapshot.get("questions");
                totalQuestions = questions.size(); // Get total number of questions

                // Initialize the adapter with questions
                adapter = new QuestionPagerAdapter(ExamAttempt.this, questions);
                viewPagerQuestions.setAdapter(adapter);

                // ViewPager page change callback to update question index
                viewPagerQuestions.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        currentQuestionIndex = position;
                        updateButtonState(); // Update button state when page changes
                    }
                });
            } else {
                // Handle error if exam not found
                timerText.setText("Exam not found");
            }
        }).addOnFailureListener(e -> {
            // Handle Firestore read failure
            timerText.setText("Error loading questions");
        });
    }

    // Method to start the countdown timer
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Update the time left in milliseconds
                timeLeftInMillis = millisUntilFinished;
                // Update the TextView with the formatted time
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Timer has finished, update the TextView or trigger an action
                timerText.setText("Time's up!");
                showSubmissionConfirmationDialog(); // Auto-submit if the time runs out
            }
        }.start(); // Start the countdown immediately
    }

    // Method to format and display the remaining time in the format HH:mm:ss
    private void updateTimer() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // Format the time as HH:mm:ss and set it on the TextView
        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerText.setText(timeFormatted);
    }

    // Toggle flag for the current question
    private void toggleFlagQuestion() {
        if (flaggedQuestions.contains(currentQuestionIndex)) {
            flaggedQuestions.remove(currentQuestionIndex); // Unflag the question
            btnFlag.setText("Flag");
            btnFlag.setBackgroundColor(getResources().getColor(R.color.defaultBtnColor)); // Reset color
        } else {
            flaggedQuestions.add(currentQuestionIndex); // Flag the question
            btnFlag.setText("Unflag");
            btnFlag.setBackgroundColor(getResources().getColor(R.color.yellow)); // Change color to yellow
        }
    }

    // Update the Next button and Flag button state
    private void updateButtonState() {
        // If we are on the last question, change the Next button to Submit
        if (currentQuestionIndex == totalQuestions - 1) {
            btnNext.setText("Submit");
        } else {
            btnNext.setText("Next");
        }

        // Update the Flag button state (whether the question is flagged or not)
        if (flaggedQuestions.contains(currentQuestionIndex)) {
            btnFlag.setText("Unflag");
            btnFlag.setBackgroundColor(getResources().getColor(R.color.yellow));
        } else {
            btnFlag.setText("Flag");
            btnFlag.setBackgroundColor(getResources().getColor(R.color.defaultBtnColor));
        }
    }

    // Show a dialog to confirm submission
    private void showSubmissionConfirmationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_exam_confirm_submission, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button backBtn = dialogView.findViewById(R.id.btnBack);
        Button submitBtn = dialogView.findViewById(R.id.btnSubmit);

        submitBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent examSummaryIntent = new Intent(ExamAttempt.this, ExamResultSummary.class);
            startActivity(examSummaryIntent);
            finish();
        });

        backBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Show grid dialog for question navigation
    private void showGridDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();  // Ensure previous dialog is dismissed if any
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_exam_summary, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        dialog = builder.create();  // Assign the dialog to the global variable

        GridLayout questionsGrid = dialogView.findViewById(R.id.questionsGrid);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        Button btnBack = dialogView.findViewById(R.id.btnBack);

        // Create buttons dynamically based on the total number of questions
        for (int i = 0; i < totalQuestions; i++) {
            Button questionButton = new Button(this);
            questionButton.setText(String.valueOf(i + 1));

            // Set the background color based on the question's status
            if (flaggedQuestions.contains(i)) {
                questionButton.setBackgroundColor(getResources().getColor(R.color.purpleAccent)); // Flagged
            } else if (userHasAttempted(i)) {
                questionButton.setBackgroundColor(getResources().getColor(R.color.green)); // Attempted
            } else {
                questionButton.setBackgroundColor(getResources().getColor(R.color.gray)); // Not Attempted
            }

            // Set click listener for each question button
            final int questionIndex = i;
            questionButton.setOnClickListener(v -> {
                viewPagerQuestions.setCurrentItem(questionIndex); // Move to the selected question
                dialog.dismiss(); // Close the dialog
            });

            // Add the button to the grid
            questionsGrid.addView(questionButton);
        }

        // Back button closes the dialog
        btnBack.setOnClickListener(v -> dialog.dismiss());

        // Submit button to submit the exam
        btnSubmit.setOnClickListener(v -> {
            dialog.dismiss();  // Dismiss the dialog before starting a new activity
            Intent examSummaryIntent = new Intent(ExamAttempt.this, ExamResultSummary.class);
            startActivity(examSummaryIntent);
            finish();
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();  // Make sure the dialog is dismissed when the activity is destroyed
        }
        super.onDestroy();
    }

    // Helper method to check if a question has been attempted
    private boolean userHasAttempted(int questionIndex) {
        // Check your logic to determine if the user has attempted this question
        // This will depend on how you track attempted questions
        return adapter.getUserAnswers()[questionIndex] != null; // Assuming null means not attempted
    }
}
