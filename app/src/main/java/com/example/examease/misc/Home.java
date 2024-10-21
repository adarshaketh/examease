package com.example.examease.misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.profile.Profile;
import com.example.examease.profile.activity_profile;
import com.example.examease.quiz.ExamResultSummary;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.Intent;
import android.widget.GridLayout;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private GridLayout gridLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize GridLayout
        gridLayout = findViewById(R.id.gridLayout);

        // Fetch categories from Firestore
        fetchCategoriesFromFirestore();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default selection (e.g., Home)
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Handle navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_profile:
                        // Launch ProfileActivity
                        Intent intent = new Intent(Home.this, activity_profile.class);
                        startActivity(intent);  // Start the profile activity
                        return true;
                }
                return false;
            }
        });
    }

    private void fetchCategoriesFromFirestore() {
        db.collection("categories")  // Replace "categories" with your Firestore collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int index = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> category = document.getData();
                            String name = category.get("name").toString();
                            String description = category.get("description").toString();

                            // Dynamically add a card to the GridLayout
                            addCardToGridLayout(name, description, index);
                            index++;
                        }
                    } else {
                        Toast.makeText(Home.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addCardToGridLayout(String name, String description, final int categoryIndex) {
        // Inflate a card view
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_item, gridLayout, false);

        // Set the title and description
        TextView cardTitle = cardView.findViewById(R.id.card_title);
        TextView cardDesc = cardView.findViewById(R.id.card_desc);
        cardTitle.setText(name);
        cardDesc.setText(description);

        // Add a click listener to open the new ExamsActivity
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ExamsActivity and pass the category index
                Intent intent = new Intent(Home.this, ListExams.class);
                intent.putExtra("categoryIndex", categoryIndex); // Pass the category index
                startActivity(intent);
            }
        });

        // Add the card to the GridLayout
        gridLayout.addView(cardView);
    }
}
