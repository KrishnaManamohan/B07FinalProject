package com.example.planetzeapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class SplashPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View firstCircle = findViewById(R.id.circleView);
        Drawable firstCircleDrawable = ContextCompat.getDrawable(this, R.drawable.circle);
        if (firstCircleDrawable instanceof GradientDrawable) {
            ((GradientDrawable) firstCircleDrawable).setColor(Color.parseColor("#373f51"));
        }
        firstCircle.setBackground(firstCircleDrawable);

        View secondCircle = findViewById(R.id.secondCircleView);
        Drawable secondCircleDrawable = ContextCompat.getDrawable(this, R.drawable.circle);
        if (secondCircleDrawable instanceof GradientDrawable) {
            ((GradientDrawable) secondCircleDrawable).setColor(Color.parseColor("#a9bcd0"));
        }
        secondCircle.setBackground(secondCircleDrawable);

        View thirdCircle = findViewById(R.id.thirdCircleView);
        Drawable thirdCircleDrawable = ContextCompat.getDrawable(this, R.drawable.circle);
        if (thirdCircleDrawable instanceof GradientDrawable) {
            ((GradientDrawable) thirdCircleDrawable).setColor(Color.parseColor("#d8dbe2"));
        }
        thirdCircle.setBackground(thirdCircleDrawable);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ObjectAnimator firstCircleUp = ObjectAnimator.ofFloat(firstCircle, "translationY", screenHeight, 200);
        ObjectAnimator secondCircleUp = ObjectAnimator.ofFloat(secondCircle, "translationY", screenHeight, 650);
        ObjectAnimator thirdCircleUp = ObjectAnimator.ofFloat(thirdCircle, "translationY", screenHeight, 1100);

        // Downward movement
        ObjectAnimator firstCircleDown = ObjectAnimator.ofFloat(firstCircle, "translationY", 200, screenHeight);
        ObjectAnimator secondCircleDown = ObjectAnimator.ofFloat(secondCircle, "translationY", 650, screenHeight);
        ObjectAnimator thirdCircleDown = ObjectAnimator.ofFloat(thirdCircle, "translationY", 1100, screenHeight);

        // Set durations for the animations
        firstCircleUp.setDuration(700);
        secondCircleUp.setDuration(1000);
        thirdCircleUp.setDuration(1300);
        firstCircleDown.setDuration(1400);
        secondCircleDown.setDuration(1000);
        thirdCircleDown.setDuration(800);

        // Set decelerate interpolators for smooth animation
        firstCircleUp.setInterpolator(new DecelerateInterpolator());
        secondCircleUp.setInterpolator(new DecelerateInterpolator());
        thirdCircleUp.setInterpolator(new DecelerateInterpolator());
        firstCircleDown.setInterpolator(new AnticipateInterpolator());
        secondCircleDown.setInterpolator(new AnticipateInterpolator());
        thirdCircleDown.setInterpolator(new AnticipateInterpolator());

        firstCircleUp.start();
        secondCircleUp.start();
        thirdCircleUp.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start downward animation for circles 3, 2, 1 (reverse order)
            thirdCircleDown.start();
            secondCircleDown.start();
            firstCircleDown.start();
        }, 1200);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashPageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3200);
    }
}