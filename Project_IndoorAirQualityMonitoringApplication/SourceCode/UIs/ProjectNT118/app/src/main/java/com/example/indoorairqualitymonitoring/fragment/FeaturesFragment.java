package com.example.indoorairqualitymonitoring.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.R;
import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.AssetService;
import com.example.indoorairqualitymonitoring.model.openweathermap.Weather;
import com.example.indoorairqualitymonitoring.model.openweathermap.WeatherFeatures;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = FeaturesFragment.class.getName();

    private TextView tvWelcome, tvDay, tvDescription;
    private TextView tvTemperature, tvSunrise, tvSunset, tvRainfall, tvHumidity, tvWindSpeed, tvPressure;
    private ImageView imgWeather;
    private View view;
    private AssetService assetService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeaturesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FeaturesFragment newInstance(String param1, String param2) {
        FeaturesFragment fragment = new FeaturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_features, container, false);
        initiate();
        displayFeatures();
        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        tvDay = view.findViewById(R.id.tvDay);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvSunrise = view.findViewById(R.id.tvSunrise);
        tvSunset = view.findViewById(R.id.tvSunset);
        tvRainfall = view.findViewById(R.id.tvRainfall);
        tvHumidity = view.findViewById(R.id.tvHumidity);
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed);
        tvPressure = view.findViewById(R.id.tvPressure);
        imgWeather = view.findViewById(R.id.imgWeather);

        // Create an API instance
        assetService = ApiClient.getClient("https://api.openweathermap.org/data/2.5/").create(AssetService.class);

        // Set username
        String welcome = getResources().getString(R.string.hi_welcome) + ", " + HomeScreen.sessionManager.getUsername();
        tvWelcome.setText(welcome);
    }

    private void displayFeatures()
    {
        Call<WeatherFeatures> call = assetService.getFeatures(getResources().getString(R.string.appid),106.803,"metric",10.8698);
        call.enqueue(new Callback<WeatherFeatures>() {
            @Override
            public void onResponse(Call<WeatherFeatures> call, Response<WeatherFeatures> response) {
                if (response.isSuccessful())
                {
                    WeatherFeatures features = response.body();
                    Calendar now = Calendar.getInstance();
                    int currHour = now.get(Calendar.HOUR_OF_DAY);
                    int currDay = now.get(Calendar.DAY_OF_WEEK);

                    // Set the day of a week
                    switch (currDay)
                    {
                        case 1:
                            tvDay.setText(getResources().getString(R.string.sunday));
                            break;

                        case 2:
                            tvDay.setText(getResources().getString(R.string.monday));
                            break;

                        case 3:
                            tvDay.setText(getResources().getString(R.string.tuesday));
                            break;

                        case 4:
                            tvDay.setText(getResources().getString(R.string.wednesday));
                            break;

                        case 5:
                            tvDay.setText(getResources().getString(R.string.thursday));
                            break;

                        case 6:
                            tvDay.setText(getResources().getString(R.string.friday));
                            break;

                        case 7:
                            tvDay.setText(getResources().getString(R.string.saturday));
                            break;
                    }

                    // Set time when sun rises or sun sets
                    tvSunrise.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(features.getSys().getSunrise() * 1000)));
                    tvSunset.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(features.getSys().getSunset() * 1000)));

                    // Set temperature, rainfall, humidity, wind speed and pressure
                    String temperature = features.getMain().getTemp() + " °C";
                    tvTemperature.setText(temperature);

                    String humidity = features.getMain().getHumidity()+"%";
                    tvHumidity.setText(humidity);

                    String windSpeed = features.getWind().getSpeed()+" km/h";
                    tvWindSpeed.setText(windSpeed);

                    if (features.getRain() == null)
                    {
                        tvRainfall.setText("0.0 mm");
                    }
                    else
                    {
                        String rainfall = features.getRain().getRainfall() + " mm";
                        tvRainfall.setText(rainfall);
                    }

                    String pressure = features.getMain().getPressure()+" hPa";
                    tvPressure.setText(pressure);

                    Weather weather = features.getWeather().get(0);
                    int weatherID = weather.getWeatherID();
                    String description = null;
                    int imageID = 0;

                    if (weatherID >= 200 && weatherID < 300)
                    {
                        description = getResources().getString(R.string.thunderstorm);
                        imageID = R.drawable.thunderstorm_icon;
                    }
                    else if (weatherID < 500)
                    {
                        description = getResources().getString(R.string.drizzle);
                        imageID = R.drawable.drizzle_icon;
                    }
                    else if (weatherID < 600)
                    {
                        description = getResources().getString(R.string.rain);

                        if (weatherID == 511)
                        {
                            imageID = R.drawable.snow_icon;
                        }

                        imageID = R.drawable.rainfall_icon;
                    }
                    else if (weatherID < 700)
                    {
                        description = getResources().getString(R.string.snow);
                        imageID = R.drawable.snow_icon;
                    }
                    else if (weatherID < 800)
                    {
                        imageID = R.drawable.atmosphere_icon;

                        if (weatherID == 701) description = getResources().getString(R.string.mist);
                        if (weatherID == 711) description = getResources().getString(R.string.smoke);
                        if (weatherID == 721) description = getResources().getString(R.string.haze);
                        if (weatherID == 731) description = getResources().getString(R.string.sandDust);
                        if (weatherID == 741) description = getResources().getString(R.string.fog);
                        if (weatherID == 751) description = getResources().getString(R.string.sand);
                        if (weatherID == 761) description = getResources().getString(R.string.dust);
                        if (weatherID == 762) description = getResources().getString(R.string.ash);
                        if (weatherID == 771) description = getResources().getString(R.string.squall);
                        if (weatherID == 781) description = getResources().getString(R.string.tornado);
                    }
                    else if (weatherID == 800)
                    {
                        description = getResources().getString(R.string.clearSky);

                        if (currHour >= 6 && currHour <= 17)
                        {
                            imageID = R.drawable.clear_day_icon;
                        }

                        imageID = R.drawable.clear_night_icon;
                    }
                    else if (weatherID == 801)
                    {
                        description = getResources().getString(R.string.clouds);

                        if (currHour >=6 && currHour <= 17)
                        {
                            imageID = R.drawable.few_clouds_day_icon;
                        }

                        imageID = R.drawable.few_clouds_night_icon;
                    }
                    else
                    {
                        description = getResources().getString(R.string.clouds);

                        if (weatherID == 802) imageID = R.drawable.scattered_clouds_icon;
                        else imageID = R.drawable.broken_overcast_clouds_icon;
                    }

                    tvDescription.setText(description);
                    imgWeather.setImageResource(imageID);
                }
            }

            @Override
            public void onFailure(Call<WeatherFeatures> call, Throwable t) {

            }
        });
    }
}