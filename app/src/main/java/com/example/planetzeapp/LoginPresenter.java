package com.example.planetzeapp;

import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.Model.LoginCallback {

    private final LoginContract.View view;
    private final LoginContract.Model model;

    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void validateLogin(String email, String password) {
        model.login(email, password, this);
    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        view.showLoginSuccess();
    }

    @Override
    public void onValidationError(String message) {
        view.showValidationError(message);
    }

    @Override
    public void onNetworkError() {
        view.showNetworkError();
    }

    @Override
    public void onInvalidCredentials() {
        view.showInvalidCredentialsError();
    }

    @Override
    public void onUserDataRetrieved(boolean annualSurveyComplete, boolean hasCountry) {
        if (annualSurveyComplete) {
            view.navigateToHomePage();
        } else if (hasCountry) {
            view.navigateToSurveyPage();
        } else {
            view.navigateToCountrySelection();
        }
    }

    @Override
    public void onError(String message) {
        view.showError(message);
    }
}
