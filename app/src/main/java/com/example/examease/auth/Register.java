package com.example.examease.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examease.R;

public class  Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void goToLogin(View v){
        Intent login = new Intent(Register.this, Login.class);
        startActivity(login);
        finish();
    }

}