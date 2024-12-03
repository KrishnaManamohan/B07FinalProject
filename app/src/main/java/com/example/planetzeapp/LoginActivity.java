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

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private LoginContract.Presenter presenter;

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

        presenter = new LoginPresenter(this, new LoginModel(FirebaseAuth.getInstance()));

        usernameEditText = findViewById(R.id.emailFieldL);
        passwordEditText = findViewById(R.id.passwordFieldL);
        loginButton = findViewById(R.id.loginButtonL);

        loginButton.setOnClickListener(v -> presenter.validateLogin(
                usernameEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim()
        ));

        findViewById(R.id.homePageButton).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StartUpActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.forgotPasswordButton).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void showValidationError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginSuccess() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHomePage() {
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToSurveyPage() {
        Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToCountrySelection() {
        Intent intent = new Intent(LoginActivity.this, CountrySelectionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInvalidCredentialsError() {
        Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
    }
}
