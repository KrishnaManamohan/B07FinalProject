package com.example.planetzeapp;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginModel implements LoginContract.Model {

    private final FirebaseAuth firebaseAuth;

    public LoginModel(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onValidationError("Please fill in all fields");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    callback.onLoginSuccess(user);
                    retrieveUserData(user.getUid(), callback);
                }
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException) {
                    callback.onNetworkError();
                } else {
                    callback.onInvalidCredentials();
                }
            }
        });
    }

    private void retrieveUserData(String userId, LoginCallback callback) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                        DataSnapshot userSnapshot = userTask.getResult();
                        String country = userSnapshot.child("country").getValue(String.class);
                        Boolean annualSurveyComplete = userSnapshot.child("AnnualSurveyComplete").getValue(Boolean.class);

                        boolean hasCountry = country != null && !country.isEmpty();
                        callback.onUserDataRetrieved(annualSurveyComplete != null && annualSurveyComplete, hasCountry);
                    } else {
                        callback.onError("Error retrieving user data.");
                    }
                });
    }
}
