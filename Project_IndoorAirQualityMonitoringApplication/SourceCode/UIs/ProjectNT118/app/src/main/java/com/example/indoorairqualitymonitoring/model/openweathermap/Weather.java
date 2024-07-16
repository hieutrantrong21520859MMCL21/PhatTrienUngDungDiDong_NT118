package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

public class Weather
{
    @SerializedName("id")
    private int id;
    public int getWeatherID() { return id;}
}