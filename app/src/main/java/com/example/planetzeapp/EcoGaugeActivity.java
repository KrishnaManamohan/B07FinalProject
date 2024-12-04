package com.example.planetzeapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            public void onDataFetched(ArrayList<Float> emissions, Map<String, Map<String, String>> surveyAnswers) {
                Log.d("Emissions", "Emissions Data: " + emissions);
                Log.d("Survey Answers", "Survey Answers: " + surveyAnswers);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error", errorMessage);
            }
        });


        findViewById(R.id.homePageButton).setOnClickListener(v -> {
            Intent intent = new Intent(EcoGaugeActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        tvTotalEmissions = findViewById(R.id.tvTotalEmissions);

        tvTotalEmissions.setText("Annual CO2 Emissions: " + FetchUserEmissionData.annualEmission + " kg");

        setupPieChart();
        ArrayList<Float> lineData = RecentDayFetcher.getLast7DaysEmissions();
        Collections.reverse(lineData);
        setupLineChart(lineChart, lineData);
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) FetchUserEmissionData.transportationEmission, "Transportation"));
        entries.add(new PieEntry((float) FetchUserEmissionData.foodEmission, "Food"));
        entries.add(new PieEntry((float) FetchUserEmissionData.purchasesEmission, "Purchases"));
        entries.add(new PieEntry((float) FetchUserEmissionData.energyEmission, "Energy"));

        int[] blackAndWhiteColors = {
                Color.BLACK,
                Color.DKGRAY,
                Color.GRAY,
                Color.LTGRAY,
                Color.WHITE
        };

        PieDataSet dataSet = new PieDataSet(entries, "CO2 Emissions");
        dataSet.setColors(blackAndWhiteColors);
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("CO2 Emissions");
        pieChart.setCenterTextSize(18f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        pieChart.invalidate();
    }

    private void setupLineChart(LineChart lineChart, ArrayList<Float> emissions) {
        if (emissions == null || emissions.isEmpty()) {
            Toast.makeText(this, "Fill Out Daily Tracking To Analyze.", Toast.LENGTH_SHORT).show();
            lineChart.setNoDataText("Fill Out Daily Tracker To Analyze Result In Graph.");
            return;
        }
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < emissions.size(); i++) {
            entries.add(new Entry(i + 1, emissions.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Emissions");
        dataSet.setColor(Color.parseColor("#009999"));
        dataSet.setValueTextColor(Color.parseColor("#1b1b1e"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setText("Last 7 Days Emissions");
        lineChart.getDescription().setTextSize(15f);
        lineChart.animateX(1000);
        lineChart.invalidate();

        lineChart.invalidate();
    }
}