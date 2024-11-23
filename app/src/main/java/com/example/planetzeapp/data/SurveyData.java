package com.example.planetzeapp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyData {
    public static List<String> getQuestions() {
        List<String> questions = new ArrayList<>();
        questions.add("1. Do you own or regularly use a car?");
        questions.add("2. What type of car do you drive?");
        questions.add("3. How many kilometers/miles do you drive per year?");
        questions.add("4. How often do you use public transportation (bus, train, subway)?");
        questions.add("5. How much time do you spend on public transport per week (bus, train, subway)?");
        questions.add("6. How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?");
        questions.add("7. How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?");
        questions.add("8. What best describes your diet?");
        questions.add("9.1. How often do you eat Beef?");
        questions.add("9.2. How often do you eat Pork?");
        questions.add("9.3. How often do you eat Chicken?");
        questions.add("9.4. How often do you eat Fish/Seafood?");
        questions.add("10. How often do you waste food or throw away uneaten leftovers?");
        questions.add("11. What type of home do you live in?");
        questions.add("12. How many people live in your household?");
        questions.add("13. What is the size of your home?");
        questions.add("14. What type of energy do you use to heat your home?");
        questions.add("15. What is your average monthly electricity bill?");
        questions.add("16. What type of energy do you use to heat water in your home?");
        questions.add("17. Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?");
        questions.add("18. How often do you buy new clothes?");
        questions.add("19. Do you buy second-hand or eco-friendly products?");
        questions.add("20. How many electronic devices (phones, laptops, etc.) have you purchased in the past year?");
        questions.add("21. How often do you recycle?");
        return questions;
    }

    public static List<List<String>> getChoices() {
        List<List<String>> choices = new ArrayList<>();

        List<String> q1Choices = new ArrayList<>();
        q1Choices.add("Yes");
        q1Choices.add("No");
        choices.add(q1Choices);

        List<String> q2Choices = new ArrayList<>();
        q2Choices.add("Gasoline");
        q2Choices.add("Diesel");
        q2Choices.add("Hybrid");
        q2Choices.add("Electric");
        q2Choices.add("I don't know");
        choices.add(q2Choices);

        List<String> q3Choices = new ArrayList<>();
        q3Choices.add("Up to 5,000 km (3,000 miles)");
        q3Choices.add("5,000–10,000 km (3,000–6,000 miles)");
        q3Choices.add("10,000–15,000 km (6,000–9,000 miles)");
        q3Choices.add("15,000–20,000 km (9,000–12,000 miles)");
        q3Choices.add("20,000–25,000 km (12,000–15,000 miles)");
        q3Choices.add("More than 25,000 km (15,000 miles)");
        choices.add(q3Choices);

        List<String> q4Choices = new ArrayList<>();
        q4Choices.add("Never");
        q4Choices.add("Occasionally (1-2 times/week)");
        q4Choices.add("Frequently (3-4 times/week)");
        q4Choices.add("Always (5+ times/week)");
        choices.add(q4Choices);

        List<String> q5Choices = new ArrayList<>();
        q5Choices.add("Under 1 hour");
        q5Choices.add("1-3 hours");
        q5Choices.add("3-5 hours");
        q5Choices.add("5-10 hours");
        q5Choices.add("More than 10 hours");
        choices.add(q5Choices);

        List<String> q6Choices = new ArrayList<>();
        q6Choices.add("None");
        q6Choices.add("1-2 flights");
        q6Choices.add("3-5 flights");
        q6Choices.add("6-10 flights");
        q6Choices.add("More than 10 flights");
        choices.add(q6Choices);

        List<String> q7Choices = new ArrayList<>();
        q7Choices.add("None");
        q7Choices.add("1-2 flights");
        q7Choices.add("3-5 flights");
        q7Choices.add("6-10 flights");
        q7Choices.add("More than 10 flights");
        choices.add(q7Choices);

        List<String> q8Choices = new ArrayList<>();
        q8Choices.add("Vegetarian");
        q8Choices.add("Vegan");
        q8Choices.add("Pescatarian (fish/seafood)");
        q8Choices.add("Meat-based (eat all types of animal products)");
        choices.add(q8Choices);

        List<String> q9_1Choices = new ArrayList<>();
        q9_1Choices.add("Daily");
        q9_1Choices.add("Frequently (3-5 times/week)");
        q9_1Choices.add("Occasionally (1-2 times/week)");
        q9_1Choices.add("Never");
        choices.add(q9_1Choices);

        List<String> q9_2Choices = new ArrayList<>();
        q9_2Choices.add("Daily");
        q9_2Choices.add("Frequently (3-5 times/week)");
        q9_2Choices.add("Occasionally (1-2 times/week)");
        q9_2Choices.add("Never");
        choices.add(q9_2Choices);

        List<String> q9_3Choices = new ArrayList<>();
        q9_3Choices.add("Daily");
        q9_3Choices.add("Frequently (3-5 times/week)");
        q9_3Choices.add("Occasionally (1-2 times/week)");
        q9_3Choices.add("Never");
        choices.add(q9_3Choices);

        List<String> q9_4Choices = new ArrayList<>();
        q9_4Choices.add("Daily");
        q9_4Choices.add("Frequently (3-5 times/week)");
        q9_4Choices.add("Occasionally (1-2 times/week)");
        q9_4Choices.add("Never");
        choices.add(q9_4Choices);

        List<String> q10Choices = new ArrayList<>();
        q10Choices.add("Never");
        q10Choices.add("Rarely");
        q10Choices.add("Occasionally");
        q10Choices.add("Frequently");
        choices.add(q10Choices);

        List<String> q11Choices = new ArrayList<>();
        q11Choices.add("Detached house");
        q11Choices.add("Semi-detached house");
        q11Choices.add("Townhouse");
        q11Choices.add("Condo/Apartment");
        q11Choices.add("Other");
        choices.add(q11Choices);

        List<String> q12Choices = new ArrayList<>();
        q12Choices.add("1");
        q12Choices.add("2");
        q12Choices.add("3-4");
        q12Choices.add("5+");
        choices.add(q12Choices);

        List<String> q13Choices = new ArrayList<>();
        q13Choices.add("Under 1000 sq. ft.");
        q13Choices.add("1000-2000 sq. ft.");
        q13Choices.add("Over 2000 sq. ft.");
        choices.add(q13Choices);

        List<String> q14Choices = new ArrayList<>();
        q14Choices.add("Natural Gas");
        q14Choices.add("Electricity");
        q14Choices.add("Oil");
        q14Choices.add("Propane");
        q14Choices.add("Wood");
        q14Choices.add("Other");
        choices.add(q14Choices);

        List<String> q15Choices = new ArrayList<>();
        q15Choices.add("Under $50");
        q15Choices.add("$50-$100");
        q15Choices.add("$100-$150");
        q15Choices.add("$150-$200");
        q15Choices.add("Over $200");
        choices.add(q15Choices);

        List<String> q16Choices = new ArrayList<>();
        q16Choices.add("Natural Gas");
        q16Choices.add("Electricity");
        q16Choices.add("Oil");
        q16Choices.add("Propane");
        q16Choices.add("Solar");
        q16Choices.add("Other");
        choices.add(q16Choices);

        List<String> q17Choices = new ArrayList<>();
        q17Choices.add("Yes, primarily (more than 50% of energy use)");
        q17Choices.add("Yes, partially (less than 50% of energy use)");
        q17Choices.add("No");
        choices.add(q17Choices);

        List<String> q18Choices = new ArrayList<>();
        q18Choices.add("Monthly");
        q18Choices.add("Quarterly");
        q18Choices.add("Annually");
        q18Choices.add("Rarely");
        choices.add(q18Choices);

        List<String> q19Choices = new ArrayList<>();
        q19Choices.add("Yes, regularly");
        q19Choices.add("Yes, occasionally");
        q19Choices.add("No");
        choices.add(q19Choices);

        List<String> q20Choices = new ArrayList<>();
        q20Choices.add("None");
        q20Choices.add("1");
        q20Choices.add("2");
        q20Choices.add("3 or more");
        choices.add(q20Choices);

        List<String> q21Choices = new ArrayList<>();
        q21Choices.add("Never");
        q21Choices.add("Occasionally");
        q21Choices.add("Frequently");
        q21Choices.add("Always");
        choices.add(q21Choices);

        return choices;
    }
}


