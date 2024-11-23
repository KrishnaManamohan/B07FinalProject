package com.example.planetzeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planetzeapp.data.SurveyData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private Map<String, String> surveyAnswers = new LinkedHashMap<>(); // Store answers temporarily
    private List<String> questions = SurveyData.getQuestions();
    private List<List<String>> choices = SurveyData.getChoices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        // Load the first question
        loadQuestion(currentQuestionIndex);

        // "Next" button functionality
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> onNextButtonClick());

        Button previousButton = findViewById(R.id.previousButton);
        previousButton.setOnClickListener(v -> onPrevButtonClick());
    }

    private void onPrevButtonClick() {
        System.out.println("Button clicked on:" + currentQuestionIndex);
        if (currentQuestionIndex > 0){

            //Apply skip logic
            currentQuestionIndex = getPreviousQuestionIndex(currentQuestionIndex, surveyAnswers);
            loadQuestion(currentQuestionIndex);
            System.out.println("Now on:" + currentQuestionIndex);
        }


    }

    private void loadQuestion(int questionIndex) {
        // Get the current question and choices
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress((int) ((currentQuestionIndex * 100) / (questions.size() - 1)));

        String question = questions.get(questionIndex);
        List<String> currentChoices = choices.get(questionIndex);

        // Set the question text
        TextView questionTextView = findViewById(R.id.questionText);
        questionTextView.setText(question);

        // Set up radio buttons dynamically for the choices
        RadioGroup choicesRadioGroup = findViewById(R.id.choicesGroup);
        choicesRadioGroup.removeAllViews();  // Remove any previous radio buttons
        choicesRadioGroup.clearCheck();

        // Create and add radio buttons
        for (String choice : currentChoices) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(choice);
            radioButton.setTextSize(16);
            choicesRadioGroup.addView(radioButton);
        }
    }

    private void onNextButtonClick() {
        // Get the selected radio button
        RadioGroup choicesRadioGroup = findViewById(R.id.choicesGroup);
        int selectedRadioButtonId = choicesRadioGroup.getCheckedRadioButtonId();
        // Check if no option is selected
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return; // Stop execution
        }

        // Fetch the selected answer
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        // Save the answer (temporarily, can be saved to Firebase later)
        surveyAnswers.put(questions.get(currentQuestionIndex), selectedAnswer);

        // Apply skip logic
        currentQuestionIndex = getNextQuestionIndex(currentQuestionIndex, selectedAnswer);

        // Check if we've reached the end of the survey
        if (currentQuestionIndex > questions.size() - 1) {
            // TODO: Save answers to Firebase here
            Toast.makeText(this, "Survey Completed!", Toast.LENGTH_SHORT).show();

            // Transition to CompleteRegistrationActivity
            Intent intent = new Intent(SurveyActivity.this, CompleteRegistrationActivity.class);
            startActivity(intent);
            finish();
        }
        if (currentQuestionIndex >= 0 && currentQuestionIndex <= questions.size() - 1){
            loadQuestion(currentQuestionIndex);
        }
    }

    private int getNextQuestionIndex(int currentIndex, String selectedAnswer) {
        if (currentIndex == 0 && selectedAnswer.equals("No")) return 3;
        if (currentIndex == 7 && !selectedAnswer.equals("Meat-based (eat all types of animal products)")) return 12;
        return currentIndex + 1;
    }

    private int getPreviousQuestionIndex(int currentIndex, Map<String, String> answers) {
        if (currentIndex == 3 && answers.get("1. Do you own or regularly use a car?").equals("No")) return 0;
        if (currentIndex == 12 && !answers.get("8. What best describes your diet?").equals("Meat-based (eat all types of animal products)")) return 7;
        return currentIndex - 1;
    }
}
