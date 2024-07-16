package com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog;

import com.google.gson.annotations.SerializedName;

public class Brightness {
    @SerializedName("value")
    private int brightnessValue;
    public int getBrightnessValue(){return brightnessValue;}
}
