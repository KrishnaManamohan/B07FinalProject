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

public class UserPassEntryActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_pass_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(v -> validateLogin());

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(UserPassEntryActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.forgotPasswordButton).setOnClickListener(v -> {
            Intent intent = new Intent(UserPassEntryActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }

    private void validateLogin() {

        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Login successful
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // Redirect to main page
                    Intent intent = new Intent(UserPassEntryActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                // Login failed
                Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}