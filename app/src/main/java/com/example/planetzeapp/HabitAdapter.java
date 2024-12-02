//package com.example.planetzeapp;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DatabaseReference;
//
//import java.util.List;
//
//public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
//
//    private List<Habit> habitList;
//    private DatabaseReference userHabitsRef;
//
//    public HabitAdapter(List<Habit> habitList, DatabaseReference userHabitsRef) {
//        this.habitList = habitList;
//        this.userHabitsRef = userHabitsRef;
//    }
//
//    @NonNull
//    @Override
//    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.habit_item, parent, false); // Use habit_item.xml
//        return new HabitViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
//        Habit habit = habitList.get(position);
//        holder.habitName.setText(habit.getName());
//        holder.habitCategory.setText(habit.getCategory());
//        holder.habitImpact.setText(String.valueOf(habit.getImpact()));
//
//        // Adopt Button functionality
//        holder.adoptButton.setOnClickListener(v -> {
//            habit.setCompleted(true); // Mark as completed locally
//            userHabitsRef.child(habit.getId()).child("isCompleted").setValue(true); // Update in Firebase
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return habitList.size();
//    }
//
//    public void updateList(List<Habit> updatedList) {
//        this.habitList = updatedList;
//        notifyDataSetChanged();
//    }
//
//    public static class HabitViewHolder extends RecyclerView.ViewHolder {
//        TextView habitName, habitCategory, habitImpact;
//        Button adoptButton;
//
//        public HabitViewHolder(@NonNull View itemView) {
//            super(itemView);
//            habitName = itemView.findViewById(R.id.habitName);
//            habitCategory = itemView.findViewById(R.id.habitCategory);
//            habitImpact = itemView.findViewById(R.id.habitScore);
//            adoptButton = itemView.findViewById(R.id.adoptButton);
//        }
//    }
//}
