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

        if (user != null) {
            mail = user.getEmail();
            if(mail != null) {
                String[] frag = mail.split("@", 2);
                userdisplay = (TextView) findViewById(R.id.usertxt);
                userdisplay.setText(frag[0]);
            } else {
                userdisplay.setText("blankuser");
            }
        }

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