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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, nameEditText, confirmpasswordEditText;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        usernameEditText = findViewById(R.id.emailField);
        passwordEditText = findViewById(R.id.passwordField);
        confirmpasswordEditText = findViewById(R.id.confirmPasswordField);
        nameEditText = findViewById(R.id.nameField);

        Button loginButton = findViewById(R.id.createButton);
        loginButton.setOnClickListener(v -> validateLogin());

        findViewById(R.id.homePageButton).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, StartUpActivity.class);
            startActivity(intent);
        });
    }

    private void validateLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String confirmedPassword = confirmpasswordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || confirmedPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmedPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
                            saveUserToDatabase(user.getUid(), username, name);
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

    private void saveUserToDatabase(String userId, String email, String fullName) {
        databaseReference.child(userId).setValue(new User(fullName, email))
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Verification email sent. " +
                        "Please check your inbox.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignUpActivity.this,
                        StartUpActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class User {
        public String fullName;
        public String email;

        public User(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }
    }
}