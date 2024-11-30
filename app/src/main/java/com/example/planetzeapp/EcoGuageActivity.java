package com.example.planetzeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class EcoGuageActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    private TextView tvTotalEmissions, tvComparison;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_guage);

        // Initialize views
        pieChartView = findViewById(R.id.pieChart);
        tvTotalEmissions = findViewById(R.id.tvTotalEmissions);
        tvComparison = findViewById(R.id.tvComparison);

        // Get logged-in user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                fetchUserIdByEmail(email);
            } else {
                Log.e("EcoGuageActivity", "Logged-in user has no email.");
                tvTotalEmissions.setText("Error: No email found for the logged-in user.");
            }
        } else {
            Log.e("EcoGuageActivity", "User is not logged in.");
            tvTotalEmissions.setText("Please log in to access your data.");
        }
    }

    private void fetchUserIdByEmail(String emailToMatch) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null && email.equals(emailToMatch)) {
                        String userId = userSnapshot.getKey();
                        Log.d("EcoGuageActivity", "Found user ID: " + userId);
                        fetchSurveyAnswers(userId);
                        return;
                    }
                }
                Log.e("EcoGuageActivity", "User with email " + emailToMatch + " not found.");
                tvTotalEmissions.setText("User data not found.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EcoGuageActivity", "Error fetching user ID: " + error.getMessage());
            }
        });
    }

    private void fetchSurveyAnswers(String userId) {
        DatabaseReference surveyReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("surveyAnswers");
        surveyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    calculateAndDisplayImpact(snapshot);
                } else {
                    Log.e("EcoGuageActivity", "No survey answers found for user ID: " + userId);
                    tvTotalEmissions.setText("No survey data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EcoGuageActivity", "Error fetching survey answers: " + error.getMessage());
            }
        });
    }

    private void calculateAndDisplayImpact(DataSnapshot snapshot) {
        // Parse survey answers
        Map<String, String> answers = (Map<String, String>) snapshot.getValue();

        if (answers == null) {
            Log.e("EcoGuageActivity", "No valid survey data to process.");
            tvTotalEmissions.setText("No valid data.");
            return;
        }

        // Calculate emissions
        float transportationImpact = calculateTransportationImpact(answers.get("2_ What type of car do you drive?"));
        float dietImpact = calculateDietImpact(answers.get("8_ What best describes your diet?"));
        float flightImpact = calculateFlightImpact(answers.get("7_ How many long-haul flights (more than 1,500 km _ 932 miles) have you taken in the past year?"));
        float energyImpact = calculateEnergyImpact(answers.get("14_ What type of energy do you use to heat your home?"));

        float totalImpact = transportationImpact + dietImpact + flightImpact + energyImpact;

        // Update the UI
        tvTotalEmissions.setText(String.format("Your total emissions this year: %.1f kg CO2e", totalImpact));
        tvComparison.setText("Comparison: Better than 60% of users.");

        // Update Pie Chart
        ArrayList<Float> values = new ArrayList<>();
        values.add(transportationImpact);
        values.add(dietImpact);
        values.add(flightImpact);
        values.add(energyImpact);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Transportation");
        labels.add("Diet");
        labels.add("Flights");
        labels.add("Energy");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(android.R.color.holo_blue_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.holo_red_light));

        pieChartView.setData(values, labels);
    }

    private float calculateTransportationImpact(String carType) {
        if (carType == null || carType.equals("Skipped")) return 0f;

        switch (carType) {
            case "Gasoline":
                return 30f;
            case "Electric":
                return 10f;
            case "Diesel":
                return 25f;
            default:
                return 0f;
        }
    }

    private float calculateDietImpact(String diet) {
        if (diet == null) return 0f;

        switch (diet) {
            case "Vegan":
                return 5f;
            case "Vegetarian":
                return 10f;
            case "Omnivorous":
                return 25f;
            default:
                return 0f;
        }
    }

    private float calculateFlightImpact(String flights) {
        if (flights == null) return 0f;

        switch (flights) {
            case "More than 10 flights":
                return 50f;
            case "3-5 flights":
                return 25f;
            case "None":
                return 0f;
            default:
                return 0f;
        }
    }

    private float calculateEnergyImpact(String energyType) {
        if (energyType == null) return 0f;

        switch (energyType) {
            case "Natural Gas":
                return 30f;
            case "Electricity":
                return 20f;
            default:
                return 0f;
        }
    }
}