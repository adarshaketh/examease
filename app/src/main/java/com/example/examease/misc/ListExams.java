package com.example.examease.misc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.examease.R;
import com.example.examease.adapter.ExamListAdapter;
import com.example.examease.quiz.Exam_guidelines;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListExams extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView examListView;
    private ArrayList<Map<String, String>> examList;
    private ExamListAdapter examListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exams);
        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        examListView = findViewById(R.id.exam_list);
        examList = new ArrayList<>();

        // Get the category index from the intent
        Intent intent = getIntent();
        int categoryIndex = intent.getIntExtra("categoryIndex", -1);

        if (categoryIndex != -1) {
            // Fetch the exams for the selected category
            fetchExamsForCategory(categoryIndex,null);

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
                            String examTitle = exam.get("title").toString();
                            String examDescription = exam.get("description").toString();
                            int examDifficulty = Integer.parseInt(exam.get("difficulty").toString());
                            String examId = exam.get("examId").toString();
                            // Filter by difficulty if it's set
                            if (difficulty == null || examDifficulty == difficulty) {
                                // Create a map to store the exam data
                                Map<String, String> examItem = new HashMap<>();
                                examItem.put("title", examTitle);
                                examItem.put("description", examDescription);
                                examItem.put("difficulty", String.valueOf(examDifficulty));
                                examItem.put("examId", examId);

                                // Add exam to the list
                                examList.add(examItem);
                            }
                        }

                        // Update the ListView with the filtered exams
                        examListAdapter = new ExamListAdapter(ListExams.this, examList);
                        examListView.setAdapter(examListAdapter);
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