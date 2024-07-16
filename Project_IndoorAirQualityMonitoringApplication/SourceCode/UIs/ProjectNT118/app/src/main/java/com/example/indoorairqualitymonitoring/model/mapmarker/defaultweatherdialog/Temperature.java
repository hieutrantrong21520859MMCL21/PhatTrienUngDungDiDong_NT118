package com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog;

import com.google.gson.annotations.SerializedName;

public class Temperature {
    @SerializedName("value")
    private float temperatureValue;
    public float getTemperatureValue(){return temperatureValue;}
}
