package com.example.planetzeapp;

import com.google.firebase.auth.FirebaseUser;

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

    interface Presenter {
        void validateLogin(String email, String password);
    }

    interface Model {
        void login(String email, String password, LoginCallback callback);

        interface LoginCallback {
            void onLoginSuccess(FirebaseUser user);
            void onValidationError(String message);
            void onNetworkError();
            void onInvalidCredentials();
            void onUserDataRetrieved(boolean annualSurveyComplete, boolean hasCountry);
            void onError(String message);
        }
    }
}
