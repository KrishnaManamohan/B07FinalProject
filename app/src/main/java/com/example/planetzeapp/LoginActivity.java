package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.emailFieldL);
        passwordEditText = findViewById(R.id.passwordFieldL);

        loginButton = findViewById(R.id.loginButtonL);
        loginButton.setOnClickListener(v -> validateLogin());

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StartUpActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.forgotPasswordButton).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
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
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // Check if user's country and survey are complete in Firebase
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .get()
                            .addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                    String country = userTask.getResult().child("country").getValue(String.class);
                                    Boolean annualSurveyComplete = userTask.getResult().child("AnnualSurveyComplete").getValue(Boolean.class);

                                    if (country != null && !country.isEmpty() && annualSurveyComplete != null) {
                                        if (annualSurveyComplete) {
                                            // Both country and survey complete, redirect to HomePage
                                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // Survey not complete, redirect to SurveyActivity
                                            Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
                                            startActivity(intent);
                                        }
                                    } else if (country != null && !country.isEmpty()) {
                                        // Country is set but survey not complete, go to SurveyActivity
                                        Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Country not set, go to CountrySelectionActivity
                                        Intent intent = new Intent(LoginActivity.this, CountrySelectionActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else {
                                    // Handle errors or null response
                                    Toast.makeText(this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                                    Log.e("FirebaseUserCheck", "Failed to retrieve user data", userTask.getException());
                                }
                            });
                }
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
                }
                Log.e("FirebaseLogin", "Login failed", e);
            }
        });
    }
}
