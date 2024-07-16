package com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog;

import com.google.gson.annotations.SerializedName;

public class Humidity {
    @SerializedName("value")
    private int humidityValue;
    public int getHumidityValue(){return humidityValue;}
}
