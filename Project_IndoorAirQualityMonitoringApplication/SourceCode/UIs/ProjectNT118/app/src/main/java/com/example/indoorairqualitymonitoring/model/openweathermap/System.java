package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

public class System
{
    @SerializedName("sunrise")
    private long sunrise;
    @SerializedName("sunset")
    private long sunset;
    public long getSunrise() {
        return sunrise;
    }
    public long getSunset() {
        return sunset;
    }
}