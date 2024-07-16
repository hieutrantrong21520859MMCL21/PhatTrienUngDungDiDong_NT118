package com.example.indoorairqualitymonitoring.model.mapmarker;

import com.google.gson.annotations.SerializedName;

public class Location
{
    @SerializedName("value")
    private Value value;
    public Value getValue()
    {
        return value;
    }
}