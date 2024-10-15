package com.example.examease;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.examease.auth.Login;
import com.example.examease.db.FirebaseHelper;
import com.example.examease.misc.Home;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        FirebaseApp.initializeApp(this);

        //Wait 2 sec.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is logged in
                if (new FirebaseHelper().isLoggedIn()) {
                    // If logged in, go to Home Page
                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);
                    finish(); // Close MainActivity so the user can't return to it
                } else {
                    // If not logged in, go to Login Page
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                    finish(); // Close MainActivity after redirecting
                }
            }
        }, 2000); // 2000 milliseconds = 2 seconds delay
    }

//                    //showCustomDialog(120);




    // Method to show the custom dialog
//    private void showCustomDialog(int numberOfQuestions) {
//        // Inflate the custom dialog layout
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View dialogView = inflater.inflate(R.layout.dialog_exam_summary, null);
//
//        // Create an AlertDialog using the inflated view
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//
//        // Find the Questions GridLayout inside the dialog
//        GridLayout questionsGrid = dialogView.findViewById(R.id.questionsGrid);
//
//        // Add question buttons dynamically to the GridLayout
//        for (int i = 1; i <= numberOfQuestions; i++) {
//            Button questionButton = new Button(this);
//            questionButton.setText(String.valueOf(i));
//            questionButton.setPadding(16, 16, 16, 16);
//            questionButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.not_attempted_color));
//
//            // Set LayoutParams for each button to ensure correct spacing
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.setMargins(8, 8, 8, 8);
//            params.width = 0;  // Ensures buttons take equal width
//            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);  // Equal width for each button
//            questionButton.setLayoutParams(params);
//
//            // Add the button to the GridLayout
//            questionsGrid.addView(questionButton);
//        }
//
//        // Show the dialog
//        dialog.show();
//    }
}
