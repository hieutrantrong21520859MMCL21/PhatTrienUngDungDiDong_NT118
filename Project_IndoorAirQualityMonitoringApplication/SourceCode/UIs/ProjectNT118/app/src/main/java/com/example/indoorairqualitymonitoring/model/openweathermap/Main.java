package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

public class Main
{
    @SerializedName("temp")
    private double temp;
    @SerializedName("pressure")
    private double pressure;
    @SerializedName("humidity")
    private double humidity;
    public double getTemp() { return temp;}
    public double getPressure() {
        return pressure;
    }
    public double getHumidity() {
        return humidity;
    }
}