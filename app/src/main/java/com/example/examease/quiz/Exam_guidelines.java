package com.example.examease.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.examease.R; // Make sure to import your R file
import com.google.android.material.button.MaterialButton;

public class Exam_guidelines extends AppCompatActivity {

    // Declare the views
    private ImageView examImageView;
    private TextView examTitleTextView;
    private MaterialButton backButton, startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_guidelines); // Link to the correct XML file

        // Initialize the views
        examImageView = findViewById(R.id.exam_image);
        examTitleTextView = findViewById(R.id.tv_exam_title);
        backButton = findViewById(R.id.btn_back);
        startButton = findViewById(R.id.btn_start);

        // Set static content for the exam title and image
        setStaticContent();

        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and navigate back
                finish();
            }
        });

        // Handle start button click
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity (e.g., the actual exam page)
                Intent startExamIntent = new Intent(Exam_guidelines.this, ExamAttempt.class);
                startActivity(startExamIntent);
            }
        });
    }

    // Function to set static content, such as the image and exam title
    private void setStaticContent() {
        // Set the exam image (replace with your illustration resource)
        examImageView.setImageResource(R.drawable.exam_illustration); // Change to your illustration

        // Set the exam title text
        examTitleTextView.setText("Flipkart Practice Test 1");
    }
}
