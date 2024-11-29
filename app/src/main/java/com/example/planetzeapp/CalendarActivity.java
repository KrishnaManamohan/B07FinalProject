package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private TextView selectedDateText;
    private Spinner q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11;
    private Button saveButton;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initializeUI();

        // Retrieve user ID from FirebaseAuth, in case didnt happen before in HomePage
        userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
        } else {
            // Fetch survey answers when user ID is available
            SurveyAnswerFetcher.fetchSurveyAnswers(userId);
        }
    }

    private void initializeUI() {
        selectedDateText = findViewById(R.id.selected_date_text);
        Button selectDateButton = findViewById(R.id.select_date_button);
        selectDateButton.setOnClickListener(v -> openDatePicker());

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveToFirebase());

        findViewById(R.id.backButtonC).setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, EcoTrackerActivity.class);
            startActivity(intent);
        });

        initializeSpinners();
    }

    private void initializeSpinners() {
        q1 = findViewById(R.id.q1Spinner);
        q2 = findViewById(R.id.q2Spinner);
        q3 = findViewById(R.id.q3Spinner);
        q4 = findViewById(R.id.q4Spinner);
        q5 = findViewById(R.id.q5Spinner);
        q6 = findViewById(R.id.q6Spinner);
        q7 = findViewById(R.id.q7Spinner);
        q8 = findViewById(R.id.q8Spinner);
        q9 = findViewById(R.id.q9Spinner);
        q10 = findViewById(R.id.q10Spinner);
        q11 = findViewById(R.id.q11Spinner);

        String[] data1 = {"0 km", "0-5 km", "5-10 km", "10-20 km", "20+ km"};
        String[] data2 = {"0 hours", "0-1 hours", "1-3 hour", "3-5 hours", "5+ hours"};
        String[] dataOther = {"0", "1", "2", "3+"};

        setupSpinner(q1, data1);
        setupSpinner(q2, data2);
        for (Spinner spinner : new Spinner[]{q3, q4, q5, q6, q7, q8, q9, q10, q11}) {
            setupSpinner(spinner, dataOther);
        }
    }

    private void setSpinnerSelection(int spinnerIndex, String answer) {
        Spinner spinner = null;
        switch (spinnerIndex) {
            case 0: spinner = q1; break;
            case 1: spinner = q2; break;
            case 2: spinner = q3; break;
            case 3: spinner = q4; break;
            case 4: spinner = q5; break;
            case 5: spinner = q6; break;
            case 6: spinner = q7; break;
            case 7: spinner = q8; break;
            case 8: spinner = q9; break;
            case 9: spinner = q10; break;
            case 10: spinner = q11; break;
        }

        // Find the position of the answer in the spinner's adapter data
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(answer);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }


    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    private void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Format the date so that the year, month, and day appear on separate lines
                    String formattedDate = "Day: " + dayOfMonth + "\nMonth: " + (monthOfYear + 1) + "\nYear: " + year;
                    selectedDateText.setText(formattedDate);

                    // Retrieve the formatted date (e.g., "2024-11-29") and load the data from Firebase
                    String formattedDateForFirebase = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    loadDataFromFirebase(formattedDateForFirebase);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void loadDataFromFirebase(String dateKey) {
        // Check if the user is logged in
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("DailySurvey")
                .child(dateKey);

        // Fetch the data for the selected date
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If data exists, populate the spinners
                Map<String, Object> data = (Map<String, Object>) task.getResult().getValue();
                if (data != null) {
                    // Populate the spinners with the stored data
                    for (int i = 0; i < 11; i++) {
                        String key = "q" + (i + 1);
                        if (data.containsKey(key)) {
                            String answer = (String) data.get(key);
                            setSpinnerSelection(i, answer);
                        }
                    }
                    // Optionally, update the daily emission if needed
                    double dailyEmission = (double) data.get("dailyEmission");
                    // Do something with dailyEmission if needed
                } else {
                    Toast.makeText(CalendarActivity.this, "No data found for the selected date.", Toast.LENGTH_SHORT).show();
                    q1.setSelection(0);
                    q2.setSelection(0);
                    q3.setSelection(0);
                    q4.setSelection(0);
                    q5.setSelection(0);
                    q6.setSelection(0);
                    q7.setSelection(0);
                    q8.setSelection(0);
                    q9.setSelection(0);
                    q10.setSelection(0);
                    q11.setSelection(0);
                }
            } else {
                Toast.makeText(CalendarActivity.this, "Failed to retrieve data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String[] getSpinnerSelections() {
        Spinner[] spinners = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
        String[] selections = new String[spinners.length];
        for (int i = 0; i < spinners.length; i++) {
            selections[i] = spinners[i].getSelectedItem().toString();
        }
        return selections;
    }


    private void saveToFirebase() {
        // Ensure a valid date is selected
        if (selectedDateText.getText().toString().equals("Selected Date: Please Choose")) {
            Toast.makeText(this, "Please select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the formatted date from the TextView
        String formattedDate = selectedDateText.getText().toString().replace("Selected Date: ", "").trim();

        // Remove the newlines for Firebase storage (if any)
        String[] dateParts = formattedDate.split("\n");
        String day = dateParts[0].split(": ")[1];   // Extract the day
        String month = dateParts[1].split(": ")[1]; // Extract the month
        String year = dateParts[2].split(": ")[1];  // Extract the year

        // Format the date as a string (e.g., "2024-11-29")
        String firebaseDateKey = year + "-" + month + "-" + day;

        // Get the reference to the user's "DailySurvey" path in Firebase
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("DailySurvey")
                .child(firebaseDateKey);  // Use the formatted date as the key

        // Create a map of the data to save
        String[] selections = getSpinnerSelections();
        String[] surveyAnswers = SurveyAnswerFetcher.getAnnualAnswers();
        double dailyEmission = EmissionCalculator.calculateDailyEmission(selections, surveyAnswers, this);
        Map<String, Object> dailySurveyData = new HashMap<>();
        dailySurveyData.put("dailyEmission", dailyEmission);

        // Add the survey answers to the map
        for (int i = 0; i < selections.length; i++) {
            dailySurveyData.put("q" + (i + 1), selections[i]);
        }

        // Save the data to Firebase
        databaseReference.setValue(dailySurveyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}





