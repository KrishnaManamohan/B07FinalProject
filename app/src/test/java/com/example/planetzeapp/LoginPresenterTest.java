package com.example.planetzeapp;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.mockito.Mockito;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View mockView;

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private DatabaseReference mockDatabaseReference;

    @Mock
    private Task<AuthResult> mockAuthResultTask;

    @Mock
    private Task<DataSnapshot> mockUserTask;

    @Mock
    private DataSnapshot mockDataSnapshot;

    private LoginPresenter presenter;
    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        presenter = new LoginPresenter(mockView, mockFirebaseAuth);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testEmptyEmailOrPasswordShowsValidationError() {
        // Test empty email and password scenarios
        presenter.validateLogin("", "password");
        verify(mockView, times(1)).showValidationError("Please fill in all fields");

        presenter.validateLogin("email@example.com", "");
        verify(mockView, times(1)).showValidationError("Please fill in all fields");
    }

    @Test
    public void testSuccessfulLoginNavigatesToHomePage() {
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            Task<AuthResult> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(true);
            listener.onComplete(task);
            return null;
        });
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("userId");

        presenter.validateLogin("email@example.com", "password");

        verify(mockView).showLoginSuccess();
        verify(mockView).navigateToHomePage();
    }

    @Test
    public void testNetworkErrorShowsNetworkError() {
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            Task<AuthResult> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(false);
            when(task.getException()).thenReturn(new Exception("Network error"));
            listener.onComplete(task);
            return null;
        });

        presenter.validateLogin("email@example.com", "password");

        verify(mockView).showNetworkError();
    }

    @Test
    public void testInvalidCredentialsShowError() {
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            Task<AuthResult> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(false);
            when(task.getException()).thenReturn(new Exception("Invalid credentials"));
            listener.onComplete(task);
            return null;
        });

        presenter.validateLogin("email@example.com", "password");

        verify(mockView).showInvalidCredentialsError();
    }

    @Test
    public void testUserProfileErrorShowsError() {
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockAuthResultTask);
        when(mockAuthResultTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            Task<AuthResult> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(true);
            listener.onComplete(task);
            return null;
        });
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("userId");

        when(mockDatabaseReference.get()).thenReturn(mockUserTask);
        when(mockUserTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            Task<DataSnapshot> task = mock(Task.class);
            when(task.isSuccessful()).thenReturn(false);
            listener.onComplete(task);
            return null;
        });

        presenter.validateLogin("email@example.com", "password");

        verify(mockView).showError("Error retrieving user data.");
    }
}
