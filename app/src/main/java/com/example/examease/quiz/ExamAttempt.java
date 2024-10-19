package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.examease.R;
import com.example.examease.adapter.QuestionPagerAdapter;

public class ExamAttempt extends AppCompatActivity {

    private ViewPager2 viewPagerQuestions;
    private Button btnNext, btnFlag;
    private ImageButton btnBack, btnGrid;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 3600000;
    private TextView timerText;

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

        // Set up ViewPager with an adapter
        QuestionPagerAdapter adapter = new QuestionPagerAdapter(this, totalQuestions);
        viewPagerQuestions.setAdapter(adapter);

        // ViewPager page change callback to update question index
        viewPagerQuestions.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentQuestionIndex = position;
            }
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> showSubmissionConfirmationDialog());

        // Grid button click listener to open a dialog
        btnGrid.setOnClickListener(v -> showGridDialog());

        // Next button click listener
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                viewPagerQuestions.setCurrentItem(currentQuestionIndex);
            }
        });

        //start timer
        startTimer();

        //back btn, show dialog
        // Register a callback for back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showSubmissionConfirmationDialog();
            }
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


    private void showGridDialog() {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_exam_summary, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            Button backBtn = dialogView.findViewById(R.id.btnBack);
            Button submitBtn = dialogView.findViewById(R.id.btnSubmit);

            AlertDialog dialog = builder.create();

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent examSummaryIntent = new Intent(ExamAttempt.this, ExamResultSummary.class);
                    startActivity(examSummaryIntent);
                    finish();
                }
            });
            dialog.show();
        }

    private void showSubmissionConfirmationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_exam_confirm_submission, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button backBtn = dialogView.findViewById(R.id.btnBack);
        Button submitBtn = dialogView.findViewById(R.id.btnSubmit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent examSummaryIntent = new Intent(ExamAttempt.this, ExamResultSummary.class);
                startActivity(examSummaryIntent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}