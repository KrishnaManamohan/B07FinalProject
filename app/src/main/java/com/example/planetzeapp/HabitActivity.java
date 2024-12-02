//package com.example.planetzeapp;
//
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.widget.EditText;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HabitActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private HabitAdapter habitAdapter;
//    private List<Habit> habitList;
//    private EditText searchBar;
//
//    private DatabaseReference userHabitsRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_habit);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            return;
//        }
//        String userId = user.getUid();
//        userHabitsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("habits");
//
//        recyclerView = findViewById(R.id.recyclerView);
//        searchBar = findViewById(R.id.searchBar);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        habitList = new ArrayList<>();
//        habitAdapter = new HabitAdapter(habitList, userHabitsRef);
//        recyclerView.setAdapter(habitAdapter);
//
//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterHabits(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        addHabitsToUser(userId);
//        fetchHabits();
//    }
//
//    private void fetchHabits() {
//        userHabitsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                habitList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Habit habit = snapshot.getValue(Habit.class);
//                    if (habit != null) {
//                        habitList.add(habit);
//                    }
//                }
//                habitAdapter.updateList(habitList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {}
//        });
//    }
//
//    private void addHabitsToUser(String userId) {
//        userHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
//                    List<Habit> predefinedHabits = new ArrayList<>();
//                    predefinedHabits.add(new Habit("Walk instead of driving", "Transportation", 4.8));
//                    predefinedHabits.add(new Habit("Use public transportation", "Transportation", 3.5));
//                    predefinedHabits.add(new Habit("Eat vegetarian meals", "Food", 2.1));
//                    predefinedHabits.add(new Habit("Unplug devices when not in use", "Energy", 1.0));
//                    predefinedHabits.add(new Habit("Recycle plastic waste", "Consumption", 1.8));
//
//                    for (Habit habit : predefinedHabits) {
//                        String key = userHabitsRef.push().getKey();
//                        if (key != null) {
//                            userHabitsRef.child(key).setValue(habit);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {}
//        });
//    }
//
//    private void filterHabits(String query) {
//        List<Habit> filteredList = new ArrayList<>();
//        for (Habit habit : habitList) {
//            if (habit.getName().toLowerCase().contains(query.toLowerCase()) ||
//                    habit.getCategory().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(habit);
//            }
//        }
//        habitAdapter.updateList(filteredList);
//    }
//}
