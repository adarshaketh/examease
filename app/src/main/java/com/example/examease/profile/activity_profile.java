package com.example.examease.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examease.R;
import com.example.examease.auth.Login;
import com.example.examease.helpers.Functions;
import com.example.examease.misc.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class activity_profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView profileName, profileEmail, examsAttempted, minutesPractice, successRate, totalExamTime;
    private RecyclerView recyclerView;
    private ExamsAdapter examsAdapter;
    private List<Exam> examList = new ArrayList<>();
    private int totalUserScore = 0;  // Track total user score across all exams
    private int totalPossibleMarks = 0;  // Track total possible marks across all exams
    private int totalExamTimeInMinutes = 0;  // Track total exam time across all exams

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views by ID
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        examsAttempted = findViewById(R.id.exams_attempted);
        minutesPractice = findViewById(R.id.minutes_practice);
        successRate = findViewById(R.id.success_rate);
        totalExamTime = findViewById(R.id.average_time);  // This is the TextView in the 4th card
        Button signOutButton = findViewById(R.id.btn_signout);
        Button homeButton = findViewById(R.id.btn_home);
        TextView editProfile = findViewById(R.id.edit_profile);  // Edit Profile Button
        recyclerView = findViewById(R.id.attempted_exams_recyclerview);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            profileEmail.setText(email);  // Set the email in the profile section
            profileName.setText(Functions.makeBold(currentUser.getDisplayName()));
            fetchExamIdsAndDetails(email);  // Fetch exam data for the current user
        }

        // Edit Profile button logic
        editProfile.setOnClickListener(v -> {
            // Show a message when Edit Profile is clicked
            Toast.makeText(activity_profile.this, "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
            // Navigate to the Edit Profile Activity
            Intent intent = new Intent(activity_profile.this, activity_edit_profile.class);
            startActivity(intent);
        });

        // Sign Out button logic
        signOutButton.setOnClickListener(v -> {
            // Sign out the user
            mAuth.signOut();

            // Clear local login data if stored (e.g., SharedPreferences)
            clearUserLoginDetails();  // Optional

            // Display a message to the user
            Toast.makeText(activity_profile.this, "Signed Out", Toast.LENGTH_SHORT).show();

            // Create an intent to navigate to the login activity
            Intent intent = new Intent(activity_profile.this, Login.class);

            // Add flags to clear the back stack and start a new task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start the login activity
            startActivity(intent);

            // Finish the profile activity
            finish();
        });

        homeButton.setOnClickListener(v -> {
            // Create an intent to navigate to the login activity
            Intent intent = new Intent(activity_profile.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    // Fetch examIds from the history and corresponding exam details from the exams collection
    private void fetchExamIdsAndDetails(String email) {
        db.collection("history")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> examIds = (List<Map<String, Object>>) documentSnapshot.get("examIds");
                        if (examIds != null) {
                            int totalDuration = 0;
                            for (Map<String, Object> examEntry : examIds) {
                                String examId = (String) examEntry.get("examId");
                                int duration = ((Long) examEntry.get("duration")).intValue();
                                long startTime = ((Long) examEntry.get("start")).longValue();  // Fetch start time
                                int score = ((Long) examEntry.get("score")).intValue();  // Fetch the user's score

                                totalDuration += duration;

                                // Fetch details for each examId and pass the start time
                                fetchExamDetails(examId, score, startTime);
                            }

                            // Display total minutes of practice (convert from seconds to minutes)
                            int minutes = totalDuration / 60;
                            minutesPractice.setText(String.valueOf(minutes));

                            // Update UI for exams attempted
                            examsAttempted.setText(String.valueOf(examIds.size()));
                        } else {
                            Toast.makeText(activity_profile.this, "No attempted exams found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity_profile.this, "No user data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity_profile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch exam details from the exams collection using the examId
    private void fetchExamDetails(String examId, int userScore, long startTime) {
        db.collection("exams")
                .document(examId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        Long totalMarks = documentSnapshot.getLong("totalMarks");
                        Long category = documentSnapshot.getLong("category");
                        Long duration = documentSnapshot.getLong("duration");  // Fetch exam duration
                        List<?> questions = (List<?>) documentSnapshot.get("questions");
                        int totalQns = (questions != null) ? questions.size() : 0;
                        // Accumulate the total exam time (duration is in minutes)
                        if (duration != null) {
                            totalExamTimeInMinutes += duration.intValue();
                        }

                        // Ensure totalMarks is not null before using it
                        if (totalMarks != null) {
                            // Accumulate the total possible marks and user score
                            totalPossibleMarks += totalMarks.intValue();
                            totalUserScore += userScore;
                        }

                        // Update success rate if totalPossibleMarks is greater than 0
                        if (totalPossibleMarks > 0) {
                            int percentage = (totalUserScore * 100) / totalPossibleMarks;
                            successRate.setText(percentage + "%");  // Update success rate in the UI
                        }

                        // Add the exam to the list
                        Exam exam = new Exam(examId, title, totalMarks != null ? totalMarks.intValue() : 0, totalQns ,String.valueOf(category), startTime);
                        examList.add(exam);

                        // Sort the list in descending order based on the start time
                        sortExamListByRecency();

                        // Update RecyclerView
                        updateRecyclerView();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity_profile.this, "Failed to load exam details.", Toast.LENGTH_SHORT).show();
                });
    }

    // Sort the exam list by recency (most recent exams first)
    private void sortExamListByRecency() {
        Collections.sort(examList, new Comparator<Exam>() {
            @Override
            public int compare(Exam o1, Exam o2) {
                return Long.compare(o2.getStartTime(), o1.getStartTime());  // Compare in descending order
            }
        });
    }

    // Update RecyclerView with the fetched exam data
    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView() {
        if (examsAdapter == null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userEmail = currentUser != null ? currentUser.getEmail() : null;

            if (userEmail != null && examList != null) {  // Ensure examList is initialized properly
                examsAdapter = new ExamsAdapter(this, examList, userEmail);  // Pass the examList and userEmail
                recyclerView.setAdapter(examsAdapter);
            } else {
                Toast.makeText(this, "Error: User email or exam list is null.", Toast.LENGTH_SHORT).show();
            }
        } else {
            examsAdapter.notifyDataSetChanged();
        }
    }

    // Optional: Method to clear user login data if stored locally (e.g., SharedPreferences)
    private void clearUserLoginDetails() {
        // Assuming you're storing user data in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all stored data
        editor.apply(); // Apply changes
    }
}
