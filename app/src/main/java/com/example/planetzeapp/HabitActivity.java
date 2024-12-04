package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HabitActivity extends AppCompatActivity {

    private AutoCompleteTextView dropdownMenu;
    private ArrayAdapter<String> adapter;

    private List<String> optionsFile1 = new ArrayList<>();
    private List<String> optionsFile2 = new ArrayList<>();
    private List<String> optionsFile3 = new ArrayList<>();
    private List<String> masterList = new ArrayList<>();

    private List<String> temporary  = new ArrayList<>();

    private LinkedHashMap<String, Integer> habitsMap = new LinkedHashMap<>();

    Map<String, Map<String, String>> last7DaysSurveys;
    List<Map<String, String>> surveyDataList;

    private DatabaseReference databaseReference;

    private static final String[] CSV_FILES = {
            "consumptionHabits.csv",
            "transportHabits.csv",
            "foodHabits.csv"
    };

    private int standingScore = 0;

    private TextView habitField1, habitField2, habitField3;
    private TextView standingField1, standingField2, standingField3;
    private ImageButton deleteHabit1, deleteHabit2, deleteHabit3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

        String userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Habits");

        dropdownMenu = findViewById(R.id.dropdownMenu);
        dropdownMenu.setThreshold(0);
        dropdownMenu.setOnClickListener(v -> dropdownMenu.showDropDown());

        habitField1 = findViewById(R.id.habitField1);
        habitField2 = findViewById(R.id.habitField2);
        habitField3 = findViewById(R.id.habitField3);

        standingField1 = findViewById(R.id.standingField1);
        standingField2 = findViewById(R.id.standingField2);
        standingField3 = findViewById(R.id.standingField3);

        deleteHabit1 = findViewById(R.id.deleteHabit1);
        deleteHabit2 = findViewById(R.id.deleteHabit2);
        deleteHabit3 = findViewById(R.id.deleteHabit3);

        habitField1.setText("Add Habit");
        habitField2.setText("Add Habit");
        habitField3.setText("Add Habit");
        standingField1.setText("--");
        standingField2.setText("--");
        standingField3.setText("--");

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


        last7DaysSurveys = RecentDayFetcher.getLast7DaysSurveys();
        surveyDataList = new ArrayList<>(last7DaysSurveys.values());
        Collections.reverse(surveyDataList);

        loadCsvFiles();

        temporary.addAll(masterList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, temporary);
        dropdownMenu.setAdapter(adapter);

        Button btnFilter1 = findViewById(R.id.btnFilter1);
        Button btnFilter2 = findViewById(R.id.btnFilter2);
        Button btnFilter3 = findViewById(R.id.btnFilter3);
        Button btnShowAll = findViewById(R.id.btnShowAll);
        ImageButton btnAddHabit = findViewById(R.id.btnAddHabit);


        btnFilter1.setOnClickListener(v -> updateDropdown(optionsFile1));
        btnFilter2.setOnClickListener(v -> updateDropdown(optionsFile2));
        btnFilter3.setOnClickListener(v -> updateDropdown(optionsFile3));
        btnShowAll.setOnClickListener(v -> updateDropdown(masterList));

        btnAddHabit.setOnClickListener(v -> addHabitToFirebase());

        findViewById(R.id.backButtonHabit).setOnClickListener(v -> {
            Intent intent = new Intent(HabitActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

    }

    private void loadCsvFiles() {
        try {
            for (int i = 0; i < CSV_FILES.length; i++) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(CSV_FILES[i])));
                String line;
                List<String> currentList = getListByIndex(i);

                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (!trimmedLine.isEmpty()) {
                        currentList.add(trimmedLine);
                        masterList.add(trimmedLine);
                    }
                }
                reader.close();
            }

            loadHabitsFromFirebase();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading CSV files: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<String> getListByIndex(int index) {
        switch (index) {
            case 0: return optionsFile1;
            case 1: return optionsFile2;
            case 2: return optionsFile3;
            default: throw new IllegalArgumentException("Invalid CSV file index");
        }
    }

    private void updateDropdown(List<String> options) {
        Log.d("HabitActivity", "MasterList: " + masterList.toString());
        Log.d("HabitActivity", "1List: " + optionsFile1.toString());
        Log.d("HabitActivity", "2List: " + optionsFile2.toString());
        Log.d("HabitActivity", "3List: " + optionsFile3.toString());
        adapter.clear();
        adapter.addAll(options);
        adapter.notifyDataSetChanged();
        dropdownMenu.showDropDown();
    }

    private void loadHabitsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    habitsMap.clear();
                    for (DataSnapshot habitSnapshot : dataSnapshot.getChildren()) {
                        String habitName = habitSnapshot.getKey();
                        Integer standing = CalculateHabits.calculateStanding(habitName, surveyDataList);
                        if (habitName != null && standing != null) {
                            habitsMap.put(habitName, standing);
                        }
                    }
                    displayHabits();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HabitActivity.this, "Error loading habits", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getStandingMessage(int standing) {
        switch (standing) {
            case -1: return "Worsening Habit";
            case 0: return "Maintaing Habit";
            case 1: return "Improving Habit";
            default: return "Unknown";
        }
    }

    private void addHabitToFirebase() {
        String selectedHabit = dropdownMenu.getText().toString();

        if (selectedHabit.isEmpty()) {
            Toast.makeText(this, "Please select a habit", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the selected habit is in the master list
        if (!masterList.contains(selectedHabit)) {
            Toast.makeText(this, "Selected habit is not in the available list", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the habit is already added
        if (habitsMap.containsKey(selectedHabit)) {
            Toast.makeText(this, "Habit already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the habit to the habits map if there's space
        if (habitsMap.size() < 3) {
            standingScore = CalculateHabits.calculateStanding(selectedHabit, surveyDataList);
            habitsMap.put(selectedHabit, standingScore);  // Default standing as 0 (Maintaining Levels)
            standingScore = 0;
            updateHabitsInFirebase();
        } else {
            Toast.makeText(this, "You can only have 3 habits", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayHabits() {
        habitField1.setText("Add Habit");
        habitField2.setText("Add Habit");
        habitField3.setText("Add Habit");
        standingField1.setText("--");
        standingField2.setText("--");
        standingField3.setText("--");

        int i = 0;
        for (Map.Entry<String, Integer> entry : habitsMap.entrySet()) {
            if (i == 0) {
                habitField1.setText(entry.getKey());
                standingField1.setText(getStandingMessage(entry.getValue()));
                deleteHabit1.setVisibility(View.VISIBLE);
                deleteHabit1.setOnClickListener(v -> deleteHabit(entry.getKey()));
            } else if (i == 1) {
                habitField2.setText(entry.getKey());
                standingField2.setText(getStandingMessage(entry.getValue()));
                deleteHabit2.setVisibility(View.VISIBLE);
                deleteHabit2.setOnClickListener(v -> deleteHabit(entry.getKey()));
            } else if (i == 2) {
                habitField3.setText(entry.getKey());
                standingField3.setText(getStandingMessage(entry.getValue()));
                deleteHabit3.setVisibility(View.VISIBLE);
                deleteHabit3.setOnClickListener(v -> deleteHabit(entry.getKey()));
            }
            i++;
        }
    }

    private void deleteHabit(String habitName) {
        habitsMap.remove(habitName);
        updateHabitsInFirebase();
        displayHabits();
    }

    private void updateHabitsInFirebase() {
        Map<String, Integer> orderedHabits = new LinkedHashMap<>(habitsMap);
        databaseReference.setValue(orderedHabits).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(HabitActivity.this, "Habit added successfully", Toast.LENGTH_SHORT).show();
                displayHabits();
            } else {
                Toast.makeText(HabitActivity.this, "Error adding habit", Toast.LENGTH_SHORT).show();
            }
        });
    }
}