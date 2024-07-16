package com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog;

import com.google.gson.annotations.SerializedName;

public class WindSpeed {
    @SerializedName("value")
    private float windSpeedValue;
    public float getWindSpeedValue(){return windSpeedValue;}
}
