package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetLink;
    private EditText emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        resetLink = findViewById(R.id.forgotPasswordButton);
        resetLink.setOnClickListener(v -> sendResetLink());

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, UserPassEntryActivity.class);
            startActivity(intent);
        });
    }

    private void sendResetLink() {
        //method to send reset link, called when button clicked
        //make sure email is entered and then it sends a link if they have an account
        String email = emailEditText.getText().toString().trim();
    }
}