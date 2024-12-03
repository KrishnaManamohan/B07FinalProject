package com.example.planetzeapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RecentDayFetcher {
    private static final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();

    private static ArrayList<Float> last7DaysEmissions = new ArrayList<>();
    private static Map<String, Map<String, String>> last7DaysSurveys = new LinkedHashMap<>();

    // Callback interface to return data asynchronously
    public interface FetchCallback {
        void onDataFetched(ArrayList<Float> emissions, Map<String, Map<String, String>> surveyAnswers);
        void onError(String errorMessage);
    }

    public static void fetchLast7Days(String userId, FetchCallback callback) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date()); // Get today's date

        databaseReference.child("Users").child(userId).child("DailySurvey")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            ArrayList<Float> emissions = new ArrayList<>();
                            Map<String, Map<String, String>> surveyAnswers = new LinkedHashMap<>();

                            // Collect keys and filter by dates within the last 7 days
                            Calendar calendar = Calendar.getInstance();
                            ArrayList<String> dateKeys = new ArrayList<>();
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String dateKey = dateSnapshot.getKey();
                                dateKeys.add(dateKey);
                            }

                            // Sort dates chronologically (oldest first)
                            dateKeys.sort((d1, d2) -> {
                                try {
                                    Date date1 = dateFormat.parse(d1);
                                    Date date2 = dateFormat.parse(d2);
                                    return date1.compareTo(date2); // Ascending order
                                } catch (Exception e) {
                                    return 0;
                                }
                            });

                            // Reverse through the dates to find up to 7 entries starting from today
                            int count = 0;
                            for (int i = dateKeys.size() - 1; i >= 0 && count < 7; i--) {
                                String dateKey = dateKeys.get(i);
                                try {
                                    Date currentDate = dateFormat.parse(today);
                                    Date keyDate = dateFormat.parse(dateKey);
                                    if (keyDate != null && !keyDate.after(currentDate)) {
                                        DataSnapshot dateSnapshot = snapshot.child(dateKey);

                                        // Fetch emissions
                                        Object emissionsValue = dateSnapshot.child("dailyEmission").getValue();
                                        if (emissionsValue != null) {
                                            emissions.add(Float.parseFloat(emissionsValue.toString()));
                                        }

                                        // Fetch survey data
                                        Map<String, String> surveyData = new LinkedHashMap<>();
                                        for (DataSnapshot surveySnapshot : dateSnapshot.getChildren()) {
                                            if (!surveySnapshot.getKey().equals("dailyEmission")) {
                                                surveyData.put(surveySnapshot.getKey(),
                                                        surveySnapshot.getValue(String.class));
                                            }
                                        }

                                        // Add to the results
                                        if (!surveyData.isEmpty()) {
                                            surveyAnswers.put(dateKey, surveyData);
                                        }
                                        count++; // Increment the count of added entries
                                    }
                                } catch (Exception e) {
                                    callback.onError("Error parsing data: " + e.getMessage());
                                    return;
                                }
                            }

                            // Save and return the results
                            Log.d("Emissions", "Fetched Emissions: " + emissions);
                            Log.d("SurveyAnswers", "Fetched Survey Answers: " + surveyAnswers);

                            last7DaysEmissions = emissions;
                            last7DaysSurveys = surveyAnswers;
                            callback.onDataFetched(emissions, surveyAnswers);
                        } else {
                            callback.onError("No data found for the user.");
                        }
                    } else {
                        callback.onError("Error fetching data: " + task.getException().getMessage());
                    }
                });
    }

    // Getter for accessing emissions data
    public static ArrayList<Float> getLast7DaysEmissions() {
        return last7DaysEmissions;
    }

    // Getter for accessing survey answers data
    public static Map<String, Map<String, String>> getLast7DaysSurveys() {
        return last7DaysSurveys;
    }
}