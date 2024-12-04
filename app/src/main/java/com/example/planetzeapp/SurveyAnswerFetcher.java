package com.example.planetzeapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SurveyAnswerFetcher {

    private static String[] annualAnswers = null;

    public static void fetchSurveyAnswers(String userId) {
        if (annualAnswers != null) {
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("surveyAnswers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> surveyAnswersList = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String answer = child.getValue(String.class);
                    if (answer != null) {
                        surveyAnswersList.add(answer);
                    }
                }

                annualAnswers = surveyAnswersList.toArray(new String[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SurveyAnswerFetcher", "Error fetching survey answers: " + error.getMessage());
            }
        });
    }

    public static String[] getAnnualAnswers() {
        return annualAnswers;
    }
}
