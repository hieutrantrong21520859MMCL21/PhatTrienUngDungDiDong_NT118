package com.example.indoorairqualitymonitoring.model.mapmarker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Value
{
    @SerializedName("coordinates")
    private List<Double> coordinates;
    public List<Double> getCoordinates()
    {
        return coordinates;
    }
}