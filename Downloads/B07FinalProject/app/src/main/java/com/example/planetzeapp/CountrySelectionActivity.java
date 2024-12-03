package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CountrySelectionActivity extends AppCompatActivity {

    private Button surveyButton;
    private AutoCompleteTextView countryDropdown;
    private DatabaseReference databaseReference;
    private String userId = null; // Firebase Auth UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_country_selection);

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and check user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Get the logged-in user's UID
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        countryDropdown = findViewById(R.id.countryField);
        surveyButton = findViewById(R.id.surveyButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Check if user already has a country set
        checkExistingCountry();

        // Set survey button action
        surveyButton.setOnClickListener(v -> confirmCountrySelection());

        // Load countries from CSV and set to dropdown
        loadCountriesFromCSV();
    }

    private void checkExistingCountry() {
        // Check if the user already has a country selected
        databaseReference.child(userId).child("country").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // If country exists, skip to SurveyActivity
                    startSurveyActivity();
                }
            } else {
                Toast.makeText(CountrySelectionActivity.this, "Failed to check country: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmCountrySelection() {
        String selectedCountry = countryDropdown.getText().toString().trim();

        if (selectedCountry.isEmpty()) {
            Toast.makeText(this, "Please select a country", Toast.LENGTH_SHORT).show();
            return;
        }

        saveCountryToFirebase(selectedCountry);
    }

    private void saveCountryToFirebase(String selectedCountry) {
        // Save selected country to Firebase under the logged-in user's UID
        databaseReference.child(userId).child("country").setValue(selectedCountry)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Proceed to SurveyActivity
                        startSurveyActivity();
                    } else {
                        Toast.makeText(CountrySelectionActivity.this, "Failed to save country", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startSurveyActivity() {
        // Navigate to SurveyActivity
        Intent intent = new Intent(CountrySelectionActivity.this, SurveyActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadCountriesFromCSV() {
        try {
            List<String> countries = loadCountriesFromAssets();

            // Set up AutoCompleteTextView with the countries
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countries);
            countryDropdown.setAdapter(adapter);

            // Show dropdown when the field is clicked or gains focus
            countryDropdown.setOnClickListener(v -> countryDropdown.showDropDown());
            countryDropdown.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    countryDropdown.showDropDown();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error loading countries: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<String> loadCountriesFromAssets() throws Exception {
        List<String> countries = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("countryList.csv")));

        String line;
        while ((line = reader.readLine()) != null) {
            countries.add(line.trim());
        }
        reader.close();
        return countries;
    }
}


