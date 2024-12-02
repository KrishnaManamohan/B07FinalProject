package com.example.planetzeapp;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmissionCalculator {

    // Store multipliers as a map (answer -> multiplier)
    private static Map<String, Double> formulaMap = new HashMap<>();
    private static double totalAnnualEmissions = 0;
    private static double transportationAnnualEmissions = 0;
    private static double foodAnnualEmissions = 0;
    private static double housingAnnualEmissions = 0;
    private static double consumptionAnnualEmissions = 0;

    // Define an array of CSV file names
    private static final String[] FORMULA_CSV_FILES = {
            "dailyFormulas.csv",
            "HousingFormula.csv",
            "transportFormulas.csv",
            "foodEmissions.csv",
            "consumptionEmission.csv"
    };

    // Initialize multipliers from the given CSV files
    public static void loadMultipliers(Context context) {
        if (!formulaMap.isEmpty()){
            return;
        }

        try {
            // Loop through all the CSV files
            for (String csvFileName : FORMULA_CSV_FILES) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(csvFileName)));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip header line if present
                    if (line.startsWith("answer")) continue;

                    String[] parts = line.split(",");
                    String answer = parts[0].trim();
                    double value = Double.parseDouble(parts[1].trim());

                    formulaMap.put(answer, value); // Map the answer to its multiplier
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double calculateAnnualEmission(List<String> surveyAnswers, Context context) {
        loadMultipliers(context);

        transportationAnnualEmissions += drivingEmissions(surveyAnswers);
        transportationAnnualEmissions += otherTransitEmissions(surveyAnswers);
        foodAnnualEmissions += foodEmissions(surveyAnswers);
        housingAnnualEmissions += housingEmissions(surveyAnswers);
        consumptionAnnualEmissions += consumptionEmissions(surveyAnswers);

        totalAnnualEmissions += transportationAnnualEmissions;
        totalAnnualEmissions += foodAnnualEmissions;
        totalAnnualEmissions += housingAnnualEmissions;
        totalAnnualEmissions += consumptionAnnualEmissions;

        return totalAnnualEmissions;
    }

    private static double consumptionEmissions(List<String> surveyAnswers) {
        double totalEmissions = 0;

        totalEmissions += formulaMap.get(surveyAnswers.get(20)+surveyAnswers.get(21));
        totalEmissions += formulaMap.get(surveyAnswers.get(22)+"Device");
        totalEmissions -= formulaMap.get(surveyAnswers.get(20)+surveyAnswers.get(23));
        totalEmissions -= formulaMap.get(surveyAnswers.get(22)+"Device"+surveyAnswers.get(23));

        if (totalEmissions < 0){
            totalEmissions = 0;
        }
        return totalEmissions;
    }

    private static double housingEmissions(List<String> surveyAnswers) {
        double totalEmissions = 0;

        totalEmissions = formulaMap.get(surveyAnswers.get(13)+surveyAnswers.get(15)+surveyAnswers.get(17)+surveyAnswers.get(16)+surveyAnswers.get(14));
        if (!surveyAnswers.get(16).equals(surveyAnswers.get(18))){
            totalEmissions += 233;
        }
        if (surveyAnswers.get(19).equals("Yes, primarily (more than 50% of energy use)")){
            totalEmissions -= 6000;
        }
        if (surveyAnswers.get(19).equals("Yes, partially (less than 50% of energy use)")){
            totalEmissions -= 4000;
        }
        if (totalEmissions < 0){
            totalEmissions = 0;
        }

        return totalEmissions;
    }

    private static double foodEmissions(List<String> surveyAnswers) {
        double totalEmissions = 0;

        if (!surveyAnswers.get(7).equals("Meat-based (eat all types of animal products)")){
            totalEmissions += formulaMap.get(surveyAnswers.get(7));
            totalEmissions += formulaMap.get(surveyAnswers.get(12)+"FoodWaste");
            return totalEmissions;
        }

        totalEmissions += formulaMap.get(surveyAnswers.get(7)+surveyAnswers.get(8)+"B");
        totalEmissions += formulaMap.get(surveyAnswers.get(7)+surveyAnswers.get(9)+"P");
        totalEmissions += formulaMap.get(surveyAnswers.get(7)+surveyAnswers.get(10)+"C");
        totalEmissions += formulaMap.get(surveyAnswers.get(7)+surveyAnswers.get(11)+"F");
        totalEmissions += formulaMap.get(surveyAnswers.get(12)+"FoodWaste");

        return totalEmissions;
    }

    private static double otherTransitEmissions(List<String> surveyAnswers) {

        double totalEmissions = 0;

        if (surveyAnswers.get(3).equals("Never") || surveyAnswers.get(5).equals("None") || surveyAnswers.get(6).equals("None")){

        }
        else {
            totalEmissions += formulaMap.get(surveyAnswers.get(3) + surveyAnswers.get(4));
            totalEmissions += formulaMap.get(surveyAnswers.get(5) + "SHF");
            totalEmissions += formulaMap.get(surveyAnswers.get(6) + "LHF");
        }

        return totalEmissions;
    }

    private static double drivingEmissions(List<String> surveyAnswers) {
        if (surveyAnswers.get(0).equals("No")){
            return 0;
        }

        int kilometers = 0;
        double totalEmission = 0.0;

        if (surveyAnswers.get(2).equals("Up to 5,000 km (3,000 miles)")){
            kilometers = 5000;
        }
        else if (surveyAnswers.get(2).equals("10,000–15,000 km (6,000–9,000 miles)")){
            kilometers = 10;
        }
        else if (surveyAnswers.get(2).equals("15,000–20,000 km (9,000–12,000 miles)")){
            kilometers = 20;
        }
        else if (surveyAnswers.get(2).equals("20,000–25,000 km (12,000–15,000 miles)")){
            kilometers = 25;
        }
        else {

        }

        if (formulaMap.containsKey(surveyAnswers.get(1))) {
            totalEmission += kilometers*formulaMap.get(surveyAnswers.get(1));
        }
        else{
            totalEmission += kilometers*.25; //average
        }

        return totalEmission;
    }

    // Calculate the daily emission based on the selections and survey answers
    public static double calculateDailyEmission(String[] selections, String[] surveyAnswers, Context context) {
        if (surveyAnswers == null) {
            return 0.0;
        }

        // Load multipliers from the available CSV files
        loadMultipliers(context);

        int kilometers = 0;
        double totalEmission = 0.0;

        if (selections[0].equals("0-5 km")){
            kilometers = 5;
        }
        else if (selections[0].equals("5-10 km")){
            kilometers = 10;
        }
        else if (selections[0].equals("10-20 km")){
            kilometers = 20;
        }
        else if (selections[0].equals("20+ km")){
            kilometers = 25;
        }

        if (formulaMap.containsKey(surveyAnswers[1])) {
            totalEmission += kilometers*formulaMap.get(surveyAnswers[1]);
        }
        else{
            totalEmission += kilometers*.25; //average
        }

        totalEmission += formulaMap.get(selections[1]);
        totalEmission += formulaMap.get("SHF"+selections[2]);
        totalEmission += formulaMap.get("LHF"+selections[3]);
        totalEmission += formulaMap.get("B"+selections[4]);
        totalEmission += formulaMap.get("C"+selections[5]);
        totalEmission += formulaMap.get("P"+selections[6]);
        totalEmission += formulaMap.get("F"+selections[7]);
        totalEmission += formulaMap.get("PB"+selections[8]);
        totalEmission += formulaMap.get("CI"+selections[9]);
        totalEmission += formulaMap.get("EI"+selections[10]);

        double housingEmission = formulaMap.get(surveyAnswers[13]+surveyAnswers[15]+surveyAnswers[17]+surveyAnswers[16]+surveyAnswers[14])/365;
        if (!surveyAnswers[16].equals(surveyAnswers[18])){
            housingEmission += 233/365;
        }
        if (surveyAnswers[19].equals("Yes, primarily (more than 50% of energy use)")){
            housingEmission -= 6000/365;
        }
        if (surveyAnswers[19].equals("Yes, partially (less than 50% of energy use)")){
            housingEmission -= 4000/365;
        }
        if (housingEmission < 0){
            housingEmission = 0;
        }
        return totalEmission + housingEmission;
    }

    // Parse the selection string to get an integer value (unchanged from your existing code)
    private static int parseOption(String selection) {
        try {
            return Integer.parseInt(selection.split(" ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double getAnnualEmissions() {
        return totalAnnualEmissions;
    }
    public static double getTransportationAnnualEmissions() {
        return transportationAnnualEmissions;
    }
    public static double getFoodAnnualEmissions() {
        return foodAnnualEmissions;
    }
    public static double getConsumptionAnnualEmissions() {
        return consumptionAnnualEmissions;
    }
    public static double getHousingAnnualEmissions() {
        return housingAnnualEmissions;
    }
}






