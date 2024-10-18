package com.example.examease.misc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;
import com.example.examease.profile.activity_edit_profile;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find the button by its ID
        Button profileButton = findViewById(R.id.profile);

        // Set an OnClickListener on the button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate to the ProfilePage activity
                Intent intent = new Intent(Home.this, activity_edit_profile.class);
                startActivity(intent);  // Start the ProfilePage activity
            }
        });
    }
}