package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EcoTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.homeButton).setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.habitsButton).setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerActivity.this, HabitActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.dailyTrackingButton).setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerActivity.this, CalendarActivity.class);
            startActivity(intent);
        });
    }
}