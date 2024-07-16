package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherFeatures
{
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("main")
    private Main main;
    @SerializedName("wind")
    private Wind wind;
    @SerializedName("rain")
    private Rain rain;
    @SerializedName("sys")
    private System sys;
    public List<Weather> getWeather() { return weather; }
    public Main getMain() {
        return main;
    }
    public Wind getWind() {
        return wind;
    }
    public Rain getRain() { return rain; }
    public System getSys() {
        return sys;
    }
}