package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

public class Wind
{
    @SerializedName("speed")
    private double speed;
    @SerializedName("deg")
    private double degree;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }
}