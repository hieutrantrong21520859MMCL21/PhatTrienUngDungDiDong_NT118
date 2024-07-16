package com.example.indoorairqualitymonitoring.model.map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapDefault
{
    @SerializedName("center")
    private List<Double> center;
    @SerializedName("zoom")
    private double zoom;
    @SerializedName("minZoom")
    private double minZoom;
    @SerializedName("maxZoom")
    private double maxZoom;

    public List<Double> getCenter() {
        return center;
    }

    public double getZoom() {
        return zoom;
    }

    public double getMinZoom() {
        return minZoom;
    }

    public double getMaxZoom() {
        return maxZoom;
    }
}