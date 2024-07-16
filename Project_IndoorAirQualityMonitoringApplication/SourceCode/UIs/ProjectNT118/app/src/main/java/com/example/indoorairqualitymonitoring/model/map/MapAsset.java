package com.example.indoorairqualitymonitoring.model.map;

import com.example.indoorairqualitymonitoring.model.map.MapOption;
import com.google.gson.annotations.SerializedName;

public class MapAsset
{
    @SerializedName("options")
    private MapOption options;
    public MapOption getOptions()
    {
        return options;
    }
}