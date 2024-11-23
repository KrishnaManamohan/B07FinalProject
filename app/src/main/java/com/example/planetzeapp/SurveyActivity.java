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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity {

    private int currentQuestionIndex = 0;
    private final Map<String, String> surveyAnswers = new LinkedHashMap<>(); // Store answers temporarily
    private final List<String> questions = SurveyData.getQuestions();
    private final List<List<String>> choices = SurveyData.getChoices();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        String userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("surveyAnswers");

        // Load the first question
        loadQuestion(currentQuestionIndex);

        // "Next" button functionality
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> onNextButtonClick());

        Button previousButton = findViewById(R.id.previousButton);
        previousButton.setOnClickListener(v -> onPrevButtonClick());
    }

    private void onPrevButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex = getPreviousQuestionIndex(currentQuestionIndex, surveyAnswers);
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
        choicesRadioGroup.removeAllViews(); // Remove any previous radio buttons
        choicesRadioGroup.clearCheck();

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

        // Save the selected answer
        surveyAnswers.put(questions.get(currentQuestionIndex), selectedAnswer);

        handleSkippingLogic(currentQuestionIndex, selectedAnswer);

        currentQuestionIndex = getNextQuestionIndex(currentQuestionIndex, selectedAnswer);

        // Check if we've reached the end of the survey
        if (currentQuestionIndex > questions.size() - 1) {
            Toast.makeText(this, "Survey Completed!", Toast.LENGTH_SHORT).show();
            saveAnswersToFirebase(); // Save to Firebase when the survey is completed
        } else {
            loadQuestion(currentQuestionIndex);
        }
    }

    private void handleSkippingLogic(int questionIndex, String selectedAnswer) {
        if (questionIndex == 0 && selectedAnswer.equals("No")) {
            markSkippedQuestions(1, 2); // Skip questions 2 and 3
        } else if (questionIndex == 7 && !selectedAnswer.equals("Meat-based (eat all types of animal products)")) {
            markSkippedQuestions(8, 11); // Skip diet-related questions
        }
    }

    private void markSkippedQuestions(int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            surveyAnswers.put(questions.get(i), "Skipped");
        }
    }

    private String sanitizeKey(String key) {
        return key.replace("/", "_")           // Replace slashes with underscores
                .replace(".", "_")           // Replace periods
                .replace("[", "_")           // Replace opening brackets
                .replace("]", "_")           // Replace closing brackets
                .replace("$", "_")           // Replace dollar signs
                .replace("#", "_")           // Replace hashes
                .replace(":", "_")           // Replace colons
                .trim()                      // Remove leading and trailing spaces
                .replaceAll("_+$", "");      // Remove trailing underscores
    }

    private void saveAnswersToFirebase() {
        // Sanitize keys before saving
        Map<String, String> sanitizedSurveyAnswers = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : surveyAnswers.entrySet()) {
            String sanitizedKey = sanitizeKey(entry.getKey());
            String sanitizedValue = entry.getValue() != null ? entry.getValue() : "Skipped";

            sanitizedSurveyAnswers.put(sanitizedKey, sanitizedValue);
        }

        databaseReference.setValue(sanitizedSurveyAnswers).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(SurveyActivity.this, CompleteRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
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