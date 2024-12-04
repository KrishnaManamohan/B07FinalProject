package com.example.planetzeapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CalculateHabits {

    public static int calculateStanding (String habit, List<Map<String, String>> survey){

        Log.d("CalculateHabits", "calculateStanding method called for habit: " + habit);

        if (survey.isEmpty() || survey == null || habit == null || survey.size() == 1){
            return 0;
        }

        if (habit.equals("Buy Less Clothes")){
            if (survey.get(survey.size()-1).get("10").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("10").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("10").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("10").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("10").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("10").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("10").substring(0,1))){
                return -1;
            }

            return 0;
        }
        if (habit.equals("Buy Less Electronics")){
            if (survey.get(survey.size()-1).get("11").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("11").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("11").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("11").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("11").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("11").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("11").substring(0,1))){
                return -1;
            }

            return 0;
        }

        if (habit.equals("No Shopping In Past Week")){
            for (int i = 0; i < survey.size(); i++){
                if (!(survey.get(i).get("10").equals("0") && survey.get(i).get("11").equals("0"))){
                    return -1;
                }
            }
            return 1;
        }

        if (habit.equals("Drive Less")){
            int today = 0;
            int yesterday = 0;

            if (survey.get(survey.size()-1).get("1").equals("0 km")){
                today = 0;
            }
            if (survey.get(survey.size()-1).get("1").equals("0-5 km")){
                today = 1;
            }
            if (survey.get(survey.size()-1).get("1").equals("5-10 km")){
                today = 2;
            }
            if (survey.get(survey.size()-1).get("1").equals("10-20 km")){
                today = 3;
            }
            if (survey.get(survey.size()-1).get("1").equals("20+ km")){
                today = 4;
            }

            if (survey.get(survey.size()-2).get("1").equals("0 km")){
                yesterday = 0;
            }
            if (survey.get(survey.size()-2).get("1").equals("0-5 km")){
                yesterday = 1;
            }
            if (survey.get(survey.size()-2).get("1").equals("5-10 km")){
                yesterday = 2;
            }
            if (survey.get(survey.size()-2).get("1").equals("10-20 km")){
                yesterday = 3;
            }
            if (survey.get(survey.size()-2).get("1").equals("20+ km")){
                yesterday = 4;
            }

            if (today < yesterday){
                return 1;
            }
            if (today == yesterday){
                return 0;
            }
            if (today > yesterday){
                return -1;
            }
            return 0;
        }

        if (habit.equals("Walk More")){
            if (survey.get(survey.size()-1).get("1").equals("0 km") && survey.get(survey.size()-1).get("2").equals("0 hours") ){
                return 1;
            }

            return 0;
        }
        if (habit.equals("Take Less Short Flights")){
            if (survey.get(survey.size()-1).get("3").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("3").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("3").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("3").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("3").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("3").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("3").substring(0,1))){
                return -1;
            }

            return 0;
        }
        if (habit.equals("Take Less Long Flights")){
            if (survey.get(survey.size()-1).get("4").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("4").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("4").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("4").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("4").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("4").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("4").substring(0,1))){
                return -1;
            }

            return 0;
        }
        if (habit.equals("Eat Less Beef")){
            if (survey.get(survey.size()-1).get("5").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("5").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("5").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("5").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("5").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("5").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("5").substring(0,1))){
                return -1;
            }

            return 0;
        }
        if (habit.equals("Eat Less Chicken")){
            if (survey.get(survey.size()-1).get("6").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("6").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("6").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("6").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("6").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("6").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("6").substring(0,1))){
                return -1;
            }

            return 0;


        }
        if (habit.equals("Eat Less Pork")){
            if (survey.get(survey.size()-1).get("7").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("7").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("7").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("7").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("7").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("7").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("7").substring(0,1))){
                return -1;
            }

            return 0;

        }
        if (habit.equals("Eat Less Fish")){
            if (survey.get(survey.size()-1).get("8").equals("0")){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("8").substring(0,1)) < Integer.parseInt(survey.get(survey.size()-2).get("8").substring(0,1))){
                return 1;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("8").substring(0,1)) == Integer.parseInt(survey.get(survey.size()-2).get("8").substring(0,1))){
                return 0;
            }
            if (Integer.parseInt(survey.get(survey.size()-1).get("8").substring(0,1)) > Integer.parseInt(survey.get(survey.size()-2).get("8").substring(0,1))){
                return -1;
            }

            return 0;
        }
        if (habit.equals("Eat Less Meat")){
            if (calculateStanding ("Eat Less Fish", survey) == 1 && calculateStanding ("Eat Less Pork", survey) == 1 && calculateStanding ("Eat Less Beef", survey) == 1 && calculateStanding ("Eat Less Chicken", survey) == 1) {
                return 1;
            }
            return -1;
        }
        if (habit.equals("Go Vegetarian")){
            if (survey.get(survey.size()-1).get("8").equals("0") && survey.get(survey.size()-1).get("7").equals("0") && survey.get(survey.size()-1).get("6").equals("0") && survey.get(survey.size()-1).get("5").equals("0")){
                return 1;
            }
            return -1;
        }

        return 0;
    }
}
