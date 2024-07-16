package com.example.indoorairqualitymonitoring.model.mapmarker;

import com.google.gson.annotations.SerializedName;

public class Asset
{
    @SerializedName("attributes")
    private Attribute attributes;
    public Attribute getAttributes()
    {
        return attributes;
    }
}