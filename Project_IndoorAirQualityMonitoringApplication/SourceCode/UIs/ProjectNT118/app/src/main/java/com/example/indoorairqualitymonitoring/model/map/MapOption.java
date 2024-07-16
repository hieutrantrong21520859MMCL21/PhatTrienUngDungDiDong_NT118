package com.example.indoorairqualitymonitoring.model.map;

import com.example.indoorairqualitymonitoring.model.map.MapDefault;
import com.google.gson.annotations.SerializedName;

public class MapOption
{
    @SerializedName("default")
    private MapDefault mapDefault;
    public MapDefault getMapDefault()
    {
        return mapDefault;
    }
}