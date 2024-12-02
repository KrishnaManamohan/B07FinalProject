package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private TextView txtcounter;
    private TextView userdisplay;
    private Button logoutButton;
    private Button ecoGaugeButton;
    private String userId;
    private String mail;
    private double annualEmission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_activty);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.habitsButton).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, HabitActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.dailyTrackingButton).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        txtcounter = findViewById(R.id.footprint);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            mail = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mail).child("fullName");

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String fullName = snapshot.getValue(String.class);
                        userdisplay = findViewById(R.id.usertxt);
                        String[] name1 = fullName.split(" ");
                        userdisplay.setText(name1[0]);  // Display user's first name
                    }
                }
            });

            // Retrieve user ID from FirebaseAuth
            userId = FirebaseAuth.getInstance().getUid();
            if (userId == null) {
                Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            } else {
                // Fetch survey answers when user ID is available
                SurveyAnswerFetcher.fetchSurveyAnswers(userId);

                // Fetch and display emission data
                FetchUserEmissionData.fetchEmissionData(userId, annualEmission -> {
                    // Callback to update UI with the fetched annual emission value
                    this.annualEmission = annualEmission;
                    update(txtcounter);
                });
            }
            RecentDayFetcher.fetchLast7Days(userId, new RecentDayFetcher.FetchCallback() {
                @Override
                public void onDataFetched(ArrayList<Float> emissions) {
                    // Handle the fetched data
                    for (Float emission : emissions) {
                        System.out.println("Emission: " + emission); // Debug: Print fetched emissions
                    }
                    updateRecentEmissionsUI(emissions); // Update the UI or chart
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(HomePageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            // Fetch and display emission data
            FetchUserEmissionData.fetchEmissionData(userId, annualEmission -> {
                // Callback to update UI with the fetched annual emission value
                this.annualEmission = annualEmission;
                update(txtcounter);
            });

            // Handle logout
            logoutButton = findViewById(R.id.logoutbutton);
            logoutButton.setOnClickListener(v -> logout());

            // Redirect to EcoGaugeActivity
            findViewById(R.id.ecoGaugeButton).setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, EcoGaugeActivity.class);
                startActivity(intent);
            });

            // Redirect to EcoTrackerActivity
            findViewById(R.id.ecoTrackerButton2).setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, EcoTrackerActivity.class);
                startActivity(intent);
            });

            findViewById(R.id.ecoBalenceButton).setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, EcoBalanceActivity.class);
                startActivity(intent);
            });
        }
    }

    private void updateRecentEmissionsUI(ArrayList<Float> emissions) {
        // Example: Display the emissions or update a chart
        StringBuilder emissionsDisplay = new StringBuilder("Last 7 Days Emissions:\n");
        for (int i = 0; i < emissions.size(); i++) {
            emissionsDisplay.append("Day ").append(i + 1).append(": ").append(emissions.get(i)).append(" kg\n");
        }
        txtcounter.setText(emissionsDisplay.toString()); // Update a TextView with the fetched data
    }

    private void update(TextView a) {
        // Update the TextView with the annual emission value
        a.setText(String.valueOf(annualEmission));  // Display the fetched emission data
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(HomePageActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
    }


}


