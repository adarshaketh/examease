package com.example.examease.misc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.examease.R;
import com.example.examease.adapter.ExamListAdapter;
import com.example.examease.db.FirebaseHelper;
import com.example.examease.helpers.Functions;
import com.example.examease.quiz.Exam_guidelines;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListExams extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView examListView;
    private ArrayList<Map<String, String>> examList;
    private ExamListAdapter examListAdapter;
    private Set<String> takenExamIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exams);
        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        examListView = findViewById(R.id.exam_list);
        examList = new ArrayList<>();

        ImageView close = findViewById(R.id.close);
        TextView heading = findViewById(R.id.heading);
        heading.setText(Functions.makeBold(getIntent().getStringExtra("title")));

        close.setOnClickListener(v -> {
            finish();
        });
        // Get the category index from the intent
        Intent intent = getIntent();
        int categoryIndex = intent.getIntExtra("categoryIndex", -1);

        if (categoryIndex != -1) {
            // Fetch the exams the user has already taken
            fetchTakenExamIds(() -> {
                // Fetch the exams for the selected category after fetching taken exams
                fetchExamsForCategory(categoryIndex, null);
            });

            // Setup difficulty filter buttons
            setupFilterButtons(categoryIndex);

            // Setup search bar
            setupSearchBar();

            // Setup ListView item click listener
            setupListViewClickListener();
        } else {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch the exam IDs that the user has already taken from history
    private void fetchTakenExamIds(Runnable onSuccess) {
        String email = new FirebaseHelper().getUserInfo().getEmail(); // Replace with your logic to get the logged-in user's email
        db.collection("history")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> examIds = (List<Map<String, Object>>) documentSnapshot.get("examIds");
                        if (examIds != null) {
                            for (Map<String, Object> examEntry : examIds) {
                                String examId = (String) examEntry.get("examId");
                                takenExamIds.add(examId); // Add the examId to the set of taken exams
                            }
                        }
                    }
                    onSuccess.run(); // Proceed to fetch exams once taken exams are fetched
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch taken exams.", Toast.LENGTH_SHORT).show();
                    onSuccess.run(); // Proceed even if fetching taken exams failed
                });
    }
    // Fetch exams for a category, excluding exams that the user has already taken
    private void fetchExamsForCategory(int categoryIndex, Integer difficulty) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Base query to get exams by category
        db.collection("exams")
                .whereEqualTo("category", categoryIndex)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        examList.clear();  // Clear the previous list of exams
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> exam = document.getData();
                            String examId = exam.get("examId").toString();

                            // Skip exams that the user has already taken
                            if (takenExamIds.contains(examId)) {
                                continue;
                            }

                            // Extract exam details
                            String examTitle = exam.get("title").toString();
                            String examDescription = exam.get("description").toString();
                            int examDifficulty = Integer.parseInt(exam.get("difficulty").toString());
                            String imageUrl = exam.get("imageUrl").toString();

                            // Filter by difficulty if it's set
                            if (difficulty == null || examDifficulty == difficulty) {
                                // Create a map to store the exam data
                                Map<String, String> examItem = new HashMap<>();
                                examItem.put("title", examTitle);
                                examItem.put("description", examDescription);
                                examItem.put("difficulty", String.valueOf(examDifficulty));
                                examItem.put("examId", examId);
                                examItem.put("imageUrl", imageUrl);

                                // Add exam to the list
                                examList.add(examItem);
                            }
                        }

                        // Update the ListView with the filtered exams
                        examListAdapter = new ExamListAdapter(ListExams.this, examList);
                        examListView.setAdapter(examListAdapter);

                        // Show or hide the "No Exams Found" message
                        TextView noExamsFound = findViewById(R.id.no_exams_found);
                        if (examList.isEmpty()) {
                            noExamsFound.setVisibility(View.VISIBLE);  // Show the message
                            examListView.setVisibility(View.GONE);     // Hide the list
                        } else {
                            noExamsFound.setVisibility(View.GONE);      // Hide the message
                            examListView.setVisibility(View.VISIBLE);   // Show the list
                        }
                    } else {
                        Toast.makeText(ListExams.this, "Error fetching exams", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setupFilterButtons(int categoryIndex) {
        TextView allTab = findViewById(R.id.all_tab);
        TextView easyTab = findViewById(R.id.easy_tab);
        TextView intermediateTab = findViewById(R.id.intermediate_tab);

        // "All" tab shows all exams for the category
        allTab.setOnClickListener(v -> fetchExamsForCategory(categoryIndex, null));

        // "Easy" tab filters by difficulty level 1
        easyTab.setOnClickListener(v -> fetchExamsForCategory(categoryIndex, 1));

        // "Intermediate" tab filters by difficulty level 2
        intermediateTab.setOnClickListener(v -> fetchExamsForCategory(categoryIndex, 2));

        // You can add more filters if needed for "Hard" etc.
    }

    private void setupSearchBar() {
        EditText searchInput = findViewById(R.id.search_input);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter exams by the search query
                filterExamsBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text is changed
            }
        });
    }

    private void filterExamsBySearch(String query) {
        ArrayList<Map<String, String>> filteredList = new ArrayList<>();

        for (Map<String, String> exam : examList) {
            String title = exam.get("title").toLowerCase();
            String description = exam.get("description").toLowerCase();

            // Check if the query matches the title or description
            if (title.contains(query.toLowerCase()) || description.contains(query.toLowerCase())) {
                filteredList.add(exam);
            }
        }

        // Update the ListView with the filtered list
        examListAdapter = new ExamListAdapter(ListExams.this, filteredList);
        examListView.setAdapter(examListAdapter);
    }

    private void setupListViewClickListener() {
        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected exam
                Map<String, String> selectedExam = examList.get(position);

                // Start ExamDetailActivity and pass exam details
                Intent intent = new Intent(ListExams.this, Exam_guidelines.class);
                intent.putExtra("examid", selectedExam.get("examId"));
                startActivity(intent);
            }
        });
    }
}