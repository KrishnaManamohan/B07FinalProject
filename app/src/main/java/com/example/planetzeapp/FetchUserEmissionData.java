package com.example.planetzeapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FetchUserEmissionData {

    public static double energyEmission = 0.0;
    public static double transportationEmission = 0.0;
    public static double foodEmission = 0.0;
    public static double purchasesEmission = 0.0;
    public static double annualEmission = 0.0;

    public static void fetchEmissionData(String userId, EmissionDataCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        ref.child("EnergyAnnualEmission").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                energyEmission = task.getResult().getValue(Double.class);
            }
            ref.child("TransportationAnnualEmission").get().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    transportationEmission = task2.getResult().getValue(Double.class);
                }
                ref.child("FoodAnnualEmission").get().addOnCompleteListener(task3 -> {
                    if (task3.isSuccessful()) {
                        foodEmission = task3.getResult().getValue(Double.class);
                    }
                    ref.child("ConsumptionAnnualEmission").get().addOnCompleteListener(task4 -> {
                        if (task4.isSuccessful()) {
                            purchasesEmission = task4.getResult().getValue(Double.class);
                        }
                        annualEmission = energyEmission + transportationEmission + foodEmission + purchasesEmission;

                        callback.onDataFetched(annualEmission);
                    });
                });
            });
        });
    }

    public interface EmissionDataCallback {
        void onDataFetched(double annualEmission);
    }
}

