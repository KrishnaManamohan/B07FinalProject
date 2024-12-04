package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planetzeapp.data.SurveyData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private final List<String> surveyAnswers = new ArrayList<>();
    private final List<String> questions = SurveyData.getQuestions();
    private final List<List<String>> choices = SurveyData.getChoices();
    private DatabaseReference databaseReference;
    double annualEmissions = 0.0;
    double transportationAnnualEmissions = 0.0;
    double foodAnnualEmissions = 0.0;
    double consumptionAnnualEmissions = 0.0;
    double energyAnnualEmissions = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        String userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId);

        loadQuestion(currentQuestionIndex);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> onNextButtonClick());

        Button previousButton = findViewById(R.id.previousButton);
        previousButton.setOnClickListener(v -> onPrevButtonClick());
    }

    private void onPrevButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex = getPreviousQuestionIndex(currentQuestionIndex);
            loadQuestion(currentQuestionIndex);
        }
    }

    private void loadQuestion(int questionIndex) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(((currentQuestionIndex * 100) / (questions.size() - 1)));

        String question = questions.get(questionIndex);
        List<String> currentChoices = choices.get(questionIndex);

        TextView questionTextView = findViewById(R.id.questionText);
        questionTextView.setText(question);

        RadioGroup choicesRadioGroup = findViewById(R.id.choicesGroup);
        choicesRadioGroup.removeAllViews();
        choicesRadioGroup.clearCheck();

        for (String choice : currentChoices) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(choice);
            radioButton.setTextSize(16);
            choicesRadioGroup.addView(radioButton);
        }
    }

    private void onNextButtonClick() {
        RadioGroup choicesRadioGroup = findViewById(R.id.choicesGroup);
        int selectedRadioButtonId = choicesRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        surveyAnswers.add(selectedAnswer);

        handleSkippingLogic(currentQuestionIndex, selectedAnswer);

        currentQuestionIndex = getNextQuestionIndex(currentQuestionIndex, selectedAnswer);

        if (currentQuestionIndex > questions.size() - 1) {
            Toast.makeText(this, "Survey Completed!", Toast.LENGTH_SHORT).show();

            annualEmissions = EmissionCalculator.calculateAnnualEmission(surveyAnswers, this);

            saveAnswersToFirebase();

        } else {
            loadQuestion(currentQuestionIndex);
        }
    }

    private void handleSkippingLogic(int questionIndex, String selectedAnswer) {
        if (questionIndex == 0 && selectedAnswer.equals("No")) {
            markSkippedQuestions(1, 2);
        } else if (questionIndex == 7 && !selectedAnswer.equals("Meat-based (eat all types of animal products)")) {
            markSkippedQuestions(8, 11);
        }
    }

    private void markSkippedQuestions(int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            surveyAnswers.add("Skipped");
        }
    }

    private void saveAnswersToFirebase() {
        databaseReference.child("surveyAnswers").setValue(surveyAnswers)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveAdditionalData();
                    } else {
                        Toast.makeText(SurveyActivity.this, "Failed to save answers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveAdditionalData() {
        databaseReference.child("AnnualSurveyComplete").setValue(true);
        databaseReference.child("FoodAnnualEmission").setValue(EmissionCalculator.getFoodAnnualEmissions());
        databaseReference.child("TransportationAnnualEmission").setValue(EmissionCalculator.getTransportationAnnualEmissions());
        databaseReference.child("EnergyAnnualEmission").setValue(EmissionCalculator.getHousingAnnualEmissions());
        databaseReference.child("ConsumptionAnnualEmission").setValue(EmissionCalculator.getConsumptionAnnualEmissions())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SurveyActivity.this, CompleteRegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SurveyActivity.this, "Failed to save additional data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getNextQuestionIndex(int currentIndex, String selectedAnswer) {
        if (currentIndex == 0 && selectedAnswer.equals("No")) return 3;
        if (currentIndex == 7 && !selectedAnswer.equals("Meat-based (eat all types of animal products)")) return 12;
        return currentIndex + 1;
    }

    private int getPreviousQuestionIndex(int currentIndex) {
        if (currentIndex == 3) return 0;
        if (currentIndex == 12) return 7;
        return currentIndex - 1;
    }
}
