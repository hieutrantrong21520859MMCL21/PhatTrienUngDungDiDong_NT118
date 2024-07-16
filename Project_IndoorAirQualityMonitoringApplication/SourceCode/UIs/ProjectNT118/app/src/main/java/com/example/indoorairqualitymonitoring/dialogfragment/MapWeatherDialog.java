package com.example.indoorairqualitymonitoring.dialogfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.R;
import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.AssetService;
import com.example.indoorairqualitymonitoring.model.mapmarker.Asset;
import com.example.indoorairqualitymonitoring.model.mapmarker.Attribute;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Humidity;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Manufacturer;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Place;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Rainfall;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.Temperature;
import com.example.indoorairqualitymonitoring.model.mapmarker.defaultweatherdialog.WindSpeed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapWeatherDialog extends DialogFragment
{
    private String token;
    private AssetService assetService;
    private TextView humidityValue, manufacturerValue, placeValue, rainFallValue, temperatureValue, windSpeedValue;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_weather_dialog, container,false);

        if (getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }

        initiate();
        setDefaultWeatherDetails();

        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        humidityValue = view.findViewById(R.id.tvHumidityValue);
        manufacturerValue = view.findViewById(R.id.tvManufacturerValue);
        placeValue = view.findViewById(R.id.tvPlaceValue);
        rainFallValue = view.findViewById(R.id.tvRainfallValue);
        temperatureValue = view.findViewById(R.id.tvTemperatureValue);
        windSpeedValue = view.findViewById(R.id.tvWindSpeedValue);

        // Create instance to call API
        assetService = ApiClient.getClient("https://uiot.ixxc.dev/").create(AssetService.class);

        // Get token
        token = HomeScreen.sessionManager.getToken();
    }
    private void setDefaultWeatherDetails()
    {
        Call<Asset> call = assetService.getAssetAttributes("Bearer " + token, getResources().getString(R.string.defaultWeatherAssetID));
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful())
                {
                    Asset asset = response.body();
                    Attribute attributes = asset.getAttributes();
                    Humidity humidity = attributes.getHumidity();
                    Manufacturer manufacturer = attributes.getManufacturer();
                    Place place = attributes.getPlace();
                    Rainfall rainfall = attributes.getRainFall();
                    Temperature temperature =attributes.getTemperature();
                    WindSpeed windSpeed = attributes.getWindSpeed();

                    humidityValue.setText(humidity.getHumidityValue()+"");
                    manufacturerValue.setText(manufacturer.getManufacturerValue());
                    placeValue.setText(place.getPlaceValue());

                    if (rainfall != null)
                    {
                        rainFallValue.setText(rainfall.getRainFallValue()+"");
                    }
                    else
                    {
                        rainFallValue.setText("0.0");
                    }

                    temperatureValue.setText(temperature.getTemperatureValue()+"");
                    windSpeedValue.setText(windSpeed.getWindSpeedValue()+"");
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {

            }
        });
    }
}