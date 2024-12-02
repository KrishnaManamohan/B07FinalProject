package com.example.planetzeapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseNetworkException;

public class LoginPresenter {

    private LoginContract.View view;
    private FirebaseAuth firebaseAuth;

    public LoginPresenter(LoginContract.View view, FirebaseAuth firebaseAuth) {
        this.view = view;
        this.firebaseAuth = firebaseAuth;
    }

    public void validateLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showValidationError("Please fill in all fields");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    view.showLoginSuccess();

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .get()
                            .addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                    DataSnapshot userSnapshot = userTask.getResult();
                                    String country = userSnapshot.child("country").getValue(String.class);
                                    Boolean annualSurveyComplete = userSnapshot.child("AnnualSurveyComplete").getValue(Boolean.class);

                                    if (country != null && !country.isEmpty() && annualSurveyComplete != null) {
                                        if (annualSurveyComplete) {
                                            view.navigateToHomePage();
                                        } else {
                                            view.navigateToSurveyPage();
                                        }
                                    } else if (country != null && !country.isEmpty()) {
                                        view.navigateToSurveyPage();
                                    } else {
                                        view.navigateToCountrySelection();
                                    }
                                } else {
                                    view.showError("Error retrieving user data.");
                                }
                            });
                }
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException) {
                    view.showNetworkError();
                } else {
                    view.showInvalidCredentialsError();
                }
            }
        });
    }
}
