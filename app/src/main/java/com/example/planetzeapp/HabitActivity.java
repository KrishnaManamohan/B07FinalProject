package com.example.planetzeapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HabitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        addHabitsToUser(userId);
    }

    private void addHabitsToUser(String userId) {
        DatabaseReference userHabitsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("habits");

        // Check if the habits list is empty
        userHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                    // Predefined habits list
                    List<Habit> predefinedHabits = new ArrayList<>();
                    predefinedHabits.add(new Habit("Walk instead of driving", "Transportation", 4.8));
                    predefinedHabits.add(new Habit("Use public transportation", "Transportation", 3.5));
                    predefinedHabits.add(new Habit("Eat vegetarian meals", "Food", 2.1));
                    predefinedHabits.add(new Habit("Unplug devices when not in use", "Energy", 1.0));
                    predefinedHabits.add(new Habit("Recycle plastic waste", "Consumption", 1.8));

                    // Push each habit to Firebase
                    for (Habit habit : predefinedHabits) {
                        String key = userHabitsRef.push().getKey();
                        userHabitsRef.child(key).setValue(habit);
                    }
                }
            }
            // Does not actually do anything but needed for ValueEventListener
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors if needed
            }

        });
    }
}
