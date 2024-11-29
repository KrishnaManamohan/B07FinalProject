package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EcoGaugeActivity extends AppCompatActivity {

    private PieChart pieChart;
    private LineChart lineChart;
    private TextView tvTotalEmissions;
    private Button btnPast7Days, btnPast30Days, btnPast90Days;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_gauge);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        RecentDayFetcher.fetchLast7Days(user.getUid(), new RecentDayFetcher.FetchCallback() {
            @Override
            public void onDataFetched(ArrayList<Float> emissions) {
                // Handle the fetched emissions here, e.g., update the chart
                setupLineChart(lineChart, emissions);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EcoGaugeActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.homePageButton).setOnClickListener(v -> {
            Intent intent = new Intent(EcoGaugeActivity.this, HomePageActivity.class);
            startActivity(intent);
        });


        // Initialize views
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        tvTotalEmissions = findViewById(R.id.tvTotalEmissions);

        // Display total emissions (from FetchUserEmissionData, assuming it's static or calculated)
        tvTotalEmissions.setText("Annual CO2 Emissions: " + FetchUserEmissionData.annualEmission + " kg");

        // Set up pie chart (with static data from FetchUserEmissionData)
        setupPieChart();
        setupLineChart(lineChart, RecentDayFetcher.getLast7DaysEmissions());

    }

    private void setupPieChart() {
        // Example data for the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) FetchUserEmissionData.transportationEmission, "Transportation"));
        entries.add(new PieEntry((float) FetchUserEmissionData.foodEmission, "Food"));
        entries.add(new PieEntry((float) FetchUserEmissionData.purchasesEmission, "Purchases"));
        entries.add(new PieEntry((float) FetchUserEmissionData.energyEmission, "Energy"));

        // Set up the dataset and colors
        PieDataSet dataSet = new PieDataSet(entries, "CO2 Emissions");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        // Set up pie data
        PieData pieData = new PieData(dataSet);

        // Configure PieChart
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("CO2 Emissions");
        pieChart.setCenterTextSize(18f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        // Refresh chart
        pieChart.invalidate();
    }

    private void setupLineChart(LineChart lineChart, ArrayList<Float> emissions) {
        if (emissions == null || emissions.isEmpty()) {
            Toast.makeText(this, "No emissions data available!", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<Entry> entries = new ArrayList<>();

        // Populate data entries
        for (int i = 0; i < emissions.size(); i++) {
            entries.add(new Entry(i + 1, emissions.get(i))); // Day i+1, emission value
        }

        // Create a dataset
        LineDataSet dataSet = new LineDataSet(entries, "Daily Emissions");
        dataSet.setColor(getResources().getColor(R.color.cyan)); // Line color
        dataSet.setValueTextColor(getResources().getColor(R.color.black)); // Text color
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        // Create a LineData object and set it to the chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize the chart
        lineChart.getDescription().setText("Last 7 Days Emissions");
        lineChart.getDescription().setTextSize(15f);
        lineChart.animateX(1000); // Animation for X-axis
        lineChart.invalidate(); // Refresh the chart

        lineChart.invalidate();
    }
}