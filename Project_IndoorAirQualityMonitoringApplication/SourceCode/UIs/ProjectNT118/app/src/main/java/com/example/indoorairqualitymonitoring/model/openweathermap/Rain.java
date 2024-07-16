package com.example.indoorairqualitymonitoring.model.openweathermap;

import com.google.gson.annotations.SerializedName;

public class Rain
{
    @SerializedName("1h")
    private double rainfall;
    public double getRainfall() { return rainfall; }
}