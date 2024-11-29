package com.example.planetzeapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecentDayFetcher {
    private static final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();


    private static ArrayList<Float> last7DaysEmissions = new ArrayList<>();

    // Callback interface to return data asynchronously
    public interface FetchCallback {
        void onDataFetched(ArrayList<Float> emissions);
        void onError(String errorMessage);
    }

    public static void fetchLast7Days(String userId, FetchCallback callback) {
        // Calculate the last 7 days' dates
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date startDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        databaseReference.child("Users").child(userId).child("DailySurvey")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            ArrayList<Float> emissions = new ArrayList<>();
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                try {
                                    String dateKey = dateSnapshot.getKey();
                                    Date surveyDate = dateFormat.parse(dateKey);
                                    if (surveyDate != null && !surveyDate.before(startDate)) {
                                        Object emissionsValue = dateSnapshot.child("dailyEmission").getValue();
                                        if (emissionsValue != null) {
                                            emissions.add(Float.parseFloat(emissionsValue.toString()));
                                        }
                                    }
                                } catch (Exception e) {
                                    callback.onError("Error parsing data: " + e.getMessage());
                                    return;
                                }
                            }
                            Log.d("Emissions", "Fetched Emissions: " + emissions);  // Debug line
                            last7DaysEmissions = emissions;
                            callback.onDataFetched(emissions);
                        } else {
                            callback.onError("No data found for the last 7 days");
                        }
                    } else {
                        callback.onError("Error fetching data: " + task.getException().getMessage());
                    }
                });

    }

    // Getter for accessing emissions data from anywhere
    public static ArrayList<Float> getLast7DaysEmissions() {
        return last7DaysEmissions;
    }
}

