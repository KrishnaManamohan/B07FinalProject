package com.example.planetzeapp;

public interface LoginContract {
    interface View {
        void showValidationError(String message);
        void showLoginSuccess();
        void navigateToHomePage();
        void navigateToSurveyPage();
        void navigateToCountrySelection();
        void showError(String message);
        void showNetworkError();
        void showInvalidCredentialsError();
    }
}
