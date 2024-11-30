package com.example.planetzeapp;

public class Habit {
    private String name;
    private String category;
    private double impact;

    public Habit() {}

    public Habit(String name, String category, double impact) {
        this.name = name;
        this.category = category;
        this.impact = impact;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }
}
