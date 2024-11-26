package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class HomePageActivity extends AppCompatActivity {

    private TextView txtcounter;
    private TextView userdisplay;
    private Button logoutButton;

    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_activty);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtcounter = (TextView)findViewById(R.id.footprint);
        update(txtcounter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        mail = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mail).child("fullName");

        databaseReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String fullName = snapshot.getValue(String.class);
                    userdisplay = (TextView) findViewById(R.id.usertxt);
                    String[] name1 = fullName.split(" ");
                    userdisplay.setText(name1[0]);
                }
            }
        });
//        if(mail != null) {
//            //String[] frag = mail.split("@", 2);
//            userdisplay = (TextView) findViewById(R.id.usertxt);
//            userdisplay.setText(mail);
//        } else {
//            userdisplay.setText("blankuser");
//        }


        logoutButton = findViewById(R.id.logoutbutton);
        logoutButton.setOnClickListener(v -> logout());

    }

    public void update(TextView a) {
        a.setText("00");
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(HomePageActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
    }
}