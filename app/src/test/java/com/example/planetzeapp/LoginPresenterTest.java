package com.example.planetzeapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import com.example.planetzeapp.LoginContract;
import com.example.planetzeapp.LoginPresenter;
import com.google.firebase.auth.FirebaseUser;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    private LoginContract.View mockView;

    @Mock
    private LoginContract.Model mockModel;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new LoginPresenter(mockView, mockModel);
    }


    @Test
    public void validateLogin_callsModelLogin_withValidInputs() {
        String email = "test@example.com";
        String password = "password123";

        presenter.validateLogin(email, password);
        verify(mockModel).login(eq(email), eq(password), any(LoginContract.Model.LoginCallback.class));
    }

    @Test
    public void onLoginSuccess_callsShowLoginSuccess_onView() {
        presenter.onLoginSuccess(mock(FirebaseUser.class));
        verify(mockView).showLoginSuccess();
    }

    @Test
    public void onValidationError_callsShowValidationError_onView() {
        presenter.onValidationError("Invalid input");
        verify(mockView).showValidationError("Invalid input");
    }

    @Test
    public void onNetworkError_callsShowNetworkError_onView() {
        presenter.onNetworkError();
        verify(mockView).showNetworkError();
    }

    @Test
    public void onInvalidCredentials_callsShowInvalidCredentialsError_onView() {
        presenter.onInvalidCredentials();
        verify(mockView).showInvalidCredentialsError();
    }

    @Test
    public void onUserDataRetrieved_navigatesToHomePage_whenSurveyComplete() {
        presenter.onUserDataRetrieved(true, false);
        verify(mockView).navigateToHomePage();
    }

    @Test
    public void onUserDataRetrieved_navigatesToSurveyPage_whenSurveyNotComplete_andCountryExists() {
        presenter.onUserDataRetrieved(false, true);
        verify(mockView).navigateToSurveyPage();
    }

    @Test
    public void onUserDataRetrieved_navigatesToCountrySelection_whenSurveyNotComplete_andNoCountryExists() {
        presenter.onUserDataRetrieved(false, false);
        verify(mockView).navigateToCountrySelection();
    }

    @Test
    public void onError_callsShowError_onView() {
        presenter.onError("An unexpected error occurred");
        verify(mockView).showError("An unexpected error occurred");
    }
}
