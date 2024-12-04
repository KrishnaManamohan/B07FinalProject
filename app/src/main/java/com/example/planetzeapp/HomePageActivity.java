package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

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
                        userdisplay.setText(name1[0]);
                    }
                }
            });

            userId = FirebaseAuth.getInstance().getUid();
            if (userId == null) {
                Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            } else {
                SurveyAnswerFetcher.fetchSurveyAnswers(userId);

                FetchUserEmissionData.fetchEmissionData(userId, annualEmission -> {
                    this.annualEmission = annualEmission;
                    update(txtcounter);
                });
            }
            RecentDayFetcher.fetchLast7Days(userId, new RecentDayFetcher.FetchCallback() {
                @Override
                public void onDataFetched(ArrayList<Float> emissions, Map<String, Map<String, String>> surveyAnswers) {
                    Log.d("Emissions", "Emissions Data: " + emissions);
                    Log.d("Survey Answers", "Survey Answers: " + surveyAnswers);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("Error", errorMessage);
                }
            });

            FetchUserEmissionData.fetchEmissionData(userId, annualEmission -> {
                this.annualEmission = annualEmission;
                update(txtcounter);
            });

            logoutButton = findViewById(R.id.logoutbutton);
            logoutButton.setOnClickListener(v -> logout());

            findViewById(R.id.ecoGaugeButton).setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, EcoGaugeActivity.class);
                startActivity(intent);
            });

            findViewById(R.id.ecoBalenceButton).setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, EcoBalanceActivity.class);
                startActivity(intent);
            });
        }
    }

    private void update(TextView a) {
        a.setText(String.valueOf(annualEmission));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(HomePageActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
    }
}