package com.example.indoorairqualitymonitoring.model.datapoint;

import com.google.gson.annotations.SerializedName;

public class AssetDatapointAttribute
{
    @SerializedName("x")
    private long timestamp;
    @SerializedName("y")
    private double attributeValue;
    public long getTimestamp()
    {
        return timestamp;
    }
    public double getAttributeValue()
    {
        return attributeValue;
    }
}