package com.example.examease.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.examease.R;
import com.example.examease.adapter.QuestionPagerAdapter;

public class ExamAttempt extends AppCompatActivity {

    private ViewPager2 viewPagerQuestions;
    private Button btnNext, btnFlag;
    private ImageButton btnBack, btnGrid;
    private TextView tvTimer;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10; // Example number of questions

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
        tvTimer = findViewById(R.id.tvTimer);

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
        btnBack.setOnClickListener(v -> finish());

        // Grid button click listener to open a dialog
        btnGrid.setOnClickListener(v -> showGridDialog());

        // Next button click listener
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                viewPagerQuestions.setCurrentItem(currentQuestionIndex);
            }
        });
    }

    // Method to show the grid dialog
    private void showGridDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_exam_summary, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Here you can add logic to display the question grid and handle item clicks

        dialog.show();
    }
}