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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateUserActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, usersFullName;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usersFullName = findViewById(R.id.userFullName);
        loginButton = findViewById(R.id.LoginButton);

        // Set button listener
        loginButton.setOnClickListener(v -> validateLogin());

        // Back button listener
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void validateLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = usersFullName.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            sendEmailVerification(user);
                        }
                    } else {
                        if (task.getException() != null) {
                            Toast.makeText(this, "Failed to create account: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    "Failed to create account due to an unknown error.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Verification email sent. " +
                        "Please check your inbox.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(CreateUserActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}