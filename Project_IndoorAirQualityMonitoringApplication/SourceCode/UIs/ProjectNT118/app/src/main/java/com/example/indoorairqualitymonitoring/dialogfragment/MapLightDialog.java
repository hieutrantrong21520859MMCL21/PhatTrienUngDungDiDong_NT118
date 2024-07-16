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
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.Brightness;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.ColourTemperature;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.Email;
import com.example.indoorairqualitymonitoring.model.mapmarker.lightdialog.OnOff;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapLightDialog extends DialogFragment
{
    private String token;
    private AssetService assetService;
    private View view;
    private TextView brightnessValue, colourTemperatureValue, emailValue, onOffValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_light_dialog, container,false);

        if (getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }

        initiate();
        setLightDetails();

        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        brightnessValue = view.findViewById(R.id.tvBrightnessValue);
        colourTemperatureValue = view.findViewById(R.id.tvColorTemperatureValue);
        emailValue = view.findViewById(R.id.tvEmailValue);
        onOffValue = view.findViewById(R.id.tvOnOffValue);

        // Create instance to call API
        assetService = ApiClient.getClient("https://uiot.ixxc.dev/").create(AssetService.class);

        // Get token
        token = HomeScreen.sessionManager.getToken();
    }
    private void setLightDetails()
    {
        Call<Asset> call = assetService.getAssetAttributes("Bearer " + token, getResources().getString(R.string.lightAssetID));
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful())
                {
                    Asset asset = response.body();
                    Attribute attributes = asset.getAttributes();
                    Brightness brightness = attributes.getBrightness();
                    ColourTemperature colourTemperature = attributes.getColourTemperature();
                    Email email = attributes.getEmail();
                    OnOff onOff = attributes.getOnOff();

                    brightnessValue.setText(brightness.getBrightnessValue()+"");
                    colourTemperatureValue.setText(colourTemperature.getColourTemperatureValue()+"");
                    emailValue.setText(email.getEmailValue());

                    if(onOff.getOnOffValue().equals("true"))
                    {
                        onOffValue.setText(getResources().getString(R.string.on));
                    }
                    else
                    {
                        onOffValue.setText(getResources().getString(R.string.off));
                    }
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {

            }
        });
    }
}