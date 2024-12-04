package com.example.planetzeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EcoBalanceActivity extends AppCompatActivity {

    private final String[][] projectDetails = {
            {"Reforestation in Kenya", "Kenya", "10,000 trees planted", "25"},
            {"Renewable Energy in India", "India", "500 homes powered", "20"},
            {"Soil Carbon Sequestration in Brazil", "Brazil", "500 hectares restored", "30"},
            {"Amazon Rainforest Restoration", "Brazil", "1 million trees planted", "30"},
            {"Bamboo Afforestation in China", "China", "Carbon sequestration using bamboo", "25"},
            {"Clean Energy Transition in India", "India", "Solar energy for 1,000 villages", "20"},
            {"Drought-Resistant Agriculture", "Ethiopia", "Sustainable farming practices", "15"},
            {"Energy Efficiency Upgrades", "Germany", "Improved building insulation", "35"},
            {"Forest Preservation", "Canada", "Protecting boreal forests", "40"},
            {"Geothermal Energy Projects", "Iceland", "Harnessing geothermal energy", "22"},
            {"Habitat Restoration", "Australia", "Rehabilitation of koala habitats", "28"},
            {"Improved Cookstoves", "Kenya", "Efficient stoves reducing emissions", "10"},
            {"Jatropha Biofuels", "Zambia", "Biofuel production from Jatropha", "18"},
            {"Kelp Forest Restoration", "California", "Ocean-based carbon sequestration", "50"},
            {"Land Reclamation", "Netherlands", "Restoring agricultural land", "30"},
            {"Mangrove Planting", "Bangladesh", "Mangrove forests for CO2 capture", "25"},
            {"Native Tree Planting", "New Zealand", "Restoring native forests", "20"},
            {"Ocean Cleanup Projects", "Global", "Removing plastic waste from oceans", "45"},
            {"Peatland Restoration", "Scotland", "Peatlands for carbon storage", "35"},
            {"Quinoa Farming Improvement", "Peru", "Low-carbon quinoa farming", "12"},
            {"Solar Energy Farms", "Morocco", "Solar farms powering communities", "30"},
            {"Tree Planting in Tanzania", "Tanzania", "15,000 trees planted annually", "20"},
            {"Urban Green Spaces", "Singapore", "Parks and green roofs", "25"},
            {"Vegetation Restoration", "Uganda", "Grasslands and shrubs planted", "18"},
            {"Waste to Energy Projects", "Sweden", "Turning waste into energy", "40"},
            {"Xeriscaping Initiatives", "Arizona", "Drought-resistant landscaping", "22"},
            {"Youth Eco-Initiatives", "South Africa", "Education and tree planting", "15"},
            {"Zero-Carbon Housing", "United Kingdom", "Energy-efficient homes", "50"}

    };

    private AutoCompleteTextView projectDropdown;
    private EditText inputTons;
    private TextView projectDescription;
    private TextView costTextView;
    private TextView totalEmissionsTextView;
    private ImageButton purchaseButton;

    private double selectedProjectCost = 0.0;
    private double totalEmissions = 100.0;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_balance);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Check if the user is logged in
        if (user == null) {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get UID
        String uid = user.getUid();

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // Initialize views
        projectDropdown = findViewById(R.id.projectDropdown);
        inputTons = findViewById(R.id.inputTons);
        projectDescription = findViewById(R.id.projectDescription);
        costTextView = findViewById(R.id.costTextView);
        totalEmissionsTextView = findViewById(R.id.totalEmissionsTextView);
        purchaseButton = findViewById(R.id.purchaseButton);

        // Synchronize local variable with Firebase data
        fetchTotalEmissions();

        // Setup dropdown for project selection
        String[] projectNames = new String[projectDetails.length];
        for (int i = 0; i < projectDetails.length; i++) {
            projectNames[i] = projectDetails[i][0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, projectNames);
        projectDropdown.setAdapter(adapter);

        // Show all project names when double-clicked
        projectDropdown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                projectDropdown.showDropDown();
            }
            return false;
        });

        projectDropdown.setOnItemClickListener((parent, view, position, id) -> displayProjectDetails(position));

        inputTons.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCostDisplay();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        purchaseButton.setOnClickListener(view -> handlePurchase());

        findViewById(R.id.back).setOnClickListener(v -> {
            Intent intent = new Intent(EcoBalanceActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }

    private void fetchTotalEmissions() {
        // Display loading state while fetching data
        totalEmissionsTextView.setText("Loading...");

        databaseReference.child("AnnualEmission").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double firebaseEmissions = snapshot.getValue(Double.class);
                    if (firebaseEmissions != null) {
                        // Check if local and Firebase values differ
                        if (firebaseEmissions != totalEmissions) {
                            totalEmissions = firebaseEmissions; // Synchronize local variable
                            totalEmissionsTextView.setText(String.format("%.2f", totalEmissions));
                            Toast.makeText(EcoBalanceActivity.this, "Synchronized with Firebase data.", Toast.LENGTH_SHORT).show();
                        } else {
                            // No difference; just display the value
                            totalEmissionsTextView.setText(String.format("%.2f", totalEmissions));
                        }
                    } else {
                        Toast.makeText(EcoBalanceActivity.this, "Firebase data is null. Retaining local value.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EcoBalanceActivity.this, "No data found in Firebase. Retaining local value.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EcoBalanceActivity.this, "Failed to fetch data from Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePurchase() {
        String selectedProject = projectDropdown.getText().toString();
        String tonsInput = inputTons.getText().toString();

        if (selectedProject.isEmpty() || selectedProjectCost == 0) {
            Toast.makeText(this, "Please select a project.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tonsInput.isEmpty()) {
            Toast.makeText(this, "Please enter the number of tons to offset.", Toast.LENGTH_SHORT).show();
            return;
        }

        double tons;
        try {
            tons = Double.parseDouble(tonsInput);
            if (tons <= 0) {
                Toast.makeText(this, "Please enter a valid number of tons.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format!", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalCost = tons * selectedProjectCost;

        if (tons > totalEmissions) {
            Toast.makeText(this, "Offset exceeds your total emissions.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the button to prevent multiple clicks
        purchaseButton.setEnabled(false);

        // Confirm purchase
        new AlertDialog.Builder(this)
                .setTitle("Confirm Purchase")
                .setMessage(String.format("You are about to purchase offsets for %.2f tons of CO2e for $%.2f. Do you want to proceed?", tons, totalCost))
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateEmissionsAfterPurchase(tons);
                    purchaseButton.setEnabled(true);
                    // Redirect to verification URL
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://planetze.io/"));
                    startActivity(browserIntent);
                })
                .setNegativeButton("No", (dialog, which) -> purchaseButton.setEnabled(true))
                .show();
    }



    private void displayProjectDetails(int position) {
        String name = projectDetails[position][0];
        String location = projectDetails[position][1];
        String impact = projectDetails[position][2];
        selectedProjectCost = Double.parseDouble(projectDetails[position][3]);

        projectDescription.setText(String.format("Project: %s\nLocation: %s\nImpact: %s\nCost: $%.2f per ton",
                name, location, impact, selectedProjectCost));

        updateCostDisplay();
    }

    private void updateCostDisplay() {
        String tonsInput = inputTons.getText().toString();
        if (!tonsInput.isEmpty() && selectedProjectCost > 0) {
            double tons = Double.parseDouble(tonsInput);
            double totalCost = tons * selectedProjectCost;
            costTextView.setText(String.format("$%.2f", totalCost));
        } else {
            costTextView.setText("$0.00");
        }
    }


    private void updateEmissionsAfterPurchase(double tons) {
        // Fetch the latest value from Firebase before updating
        databaseReference.child("AnnualEmission").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double currentEmissions = snapshot.getValue(Double.class) != null ? snapshot.getValue(Double.class) : 0.0;

                // Calculate the updated emissions
                double updatedEmissions = currentEmissions - tons;

                // Update Firebase
                databaseReference.child("AnnualEmission").setValue(updatedEmissions)
                        .addOnSuccessListener(aVoid -> {
                            // Update local value and UI
                            totalEmissions = updatedEmissions;
                            totalEmissionsTextView.setText(String.format("%.2f", totalEmissions));
                            Toast.makeText(EcoBalanceActivity.this, "Purchase Successful! Emissions updated.", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EcoBalanceActivity.this, "Failed to update emissions data.", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EcoBalanceActivity.this, "Failed to load current emissions data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}