package com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog;

import com.google.gson.annotations.SerializedName;

public class Rainfall {
    @SerializedName("value")
    private float rainFallValue;
    public float getRainFallValue(){return rainFallValue;}
}
