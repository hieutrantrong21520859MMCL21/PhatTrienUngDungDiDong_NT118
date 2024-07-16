package com.example.indoorairqualitymonitoring.model.mapmarker;

import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Humidity;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Manufacturer;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Place;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Rainfall;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Temperature;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.WindSpeed;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.Brightness;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.ColourTemperature;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.Email;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.OnOff;
import com.google.gson.annotations.SerializedName;

public class Attribute
{
    @SerializedName("location")
    private Location location;
    //DefaultWeatherDialog
    @SerializedName("humidity")
    private Humidity humidity;
    @SerializedName("manufacturer")
    private Manufacturer manufacturer;
    @SerializedName("place")
    private Place place;
    @SerializedName("windSpeed")
    private WindSpeed windSpeed;
    @SerializedName("rainfall")
    private Rainfall rainFall;
    @SerializedName("temperature")
    private Temperature temperature;

    public Temperature getTemperature() {
        return temperature;
    }

    public Place getPlace() {
        return place;
    }

    public WindSpeed getWindSpeed() {
        return windSpeed;
    }

    public Rainfall getRainFall() {
        return rainFall;
    }
    public Location getLocation() {
        return location;
    }
    public Humidity getHumidity(){
        return  humidity;
    }

    //LightDialog
    public Manufacturer getManufacturer() {
        return manufacturer;
    }
    @SerializedName("brightness")
    private Brightness brightness;
    @SerializedName("colourTemperature")
    private ColourTemperature colourTemperature;
    @SerializedName("email")
    private Email email;
    @SerializedName("onOff")
    private OnOff onOff;
    public Brightness getBrightness() {
        return brightness;
    }

    public ColourTemperature getColourTemperature() {
        return colourTemperature;
    }

    public Email getEmail() {
        return email;
    }

    public OnOff getOnOff() {
        return onOff;
    }
}