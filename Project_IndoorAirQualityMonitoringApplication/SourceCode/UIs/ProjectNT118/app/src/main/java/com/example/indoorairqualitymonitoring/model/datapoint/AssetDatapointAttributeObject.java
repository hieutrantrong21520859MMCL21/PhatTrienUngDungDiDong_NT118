package com.example.indoorairqualitymonitoring.model.datapoint;

import com.google.gson.annotations.SerializedName;

public class AssetDatapointAttributeObject
{
    @SerializedName("type")
    private String type;
    @SerializedName("fromTimestamp")
    private long fromTimestamp;
    @SerializedName("toTimestamp")
    private long toTimestamp;
    @SerializedName("amountOfPoints")
    private int amountOfPoints;

    public AssetDatapointAttributeObject(String type, long fromTimestamp, long toTimestamp, int amountOfPoints)
    {
        this.type = type;
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.amountOfPoints = amountOfPoints;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(long fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public long getToTimestamp() {
        return toTimestamp;
    }

    public void setToTimestamp(long toTimestamp) {
        this.toTimestamp = toTimestamp;
    }

    public int getAmountOfPoints() {
        return amountOfPoints;
    }

    public void setAmountOfPoints(int amountOfPoints) {
        this.amountOfPoints = amountOfPoints;
    }
}