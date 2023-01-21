package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;


public class NoteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent      = getIntent();
        String title       = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String location    = intent.getStringExtra("location");

        TextView titleView       = findViewById(R.id.title);
        TextView descriptionView = findViewById(R.id.description);
        TextView locationView    = findViewById(R.id.location);

        titleView.setText(title);
        descriptionView.setText(description);
        locationView.setText(location);




    }
}