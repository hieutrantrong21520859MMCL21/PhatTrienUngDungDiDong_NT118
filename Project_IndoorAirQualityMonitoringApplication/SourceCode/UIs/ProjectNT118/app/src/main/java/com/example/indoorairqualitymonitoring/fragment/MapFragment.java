package com.example.indoorairqualitymonitoring.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ZoomControls;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.R;
import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.AssetService;
import com.example.indoorairqualitymonitoring.dialogfragment.MapLightDialog;
import com.example.indoorairqualitymonitoring.dialogfragment.MapWeatherDialog;
import com.example.indoorairqualitymonitoring.model.mapmarker.Asset;
import com.example.indoorairqualitymonitoring.model.mapmarker.Attribute;
import com.example.indoorairqualitymonitoring.model.mapmarker.Location;
import com.example.indoorairqualitymonitoring.model.map.MapAsset;
import com.example.indoorairqualitymonitoring.model.map.MapDefault;
import com.example.indoorairqualitymonitoring.model.map.MapOption;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = MapFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final String SHARED_PREFS_NAME = "map";
    private MapView map;
    private ZoomControls zoomControls;
    private Context context;
    private SharedPreferences shrPrefs;
    private String token;
    private View view;
    private AssetService assetService;

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getBaseContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        map = view.findViewById(R.id.mapView);
        zoomControls = view.findViewById(R.id.zoomControls);
        shrPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Create instance to call API
        assetService = ApiClient.getClient("https://uiot.ixxc.dev/").create(AssetService.class);

        // Get token
        token = HomeScreen.sessionManager.getToken();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load the osmdroid configuration from shared preferences
        Configuration.getInstance().load(context, shrPrefs);

        // Setup map settings
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

/*
        IMapController mapController = map.getController();
        mapController.setZoom(18.5);
        GeoPoint startPoint = new GeoPoint(10.87, 106.80324);
        mapController.setCenter(startPoint);*/

        //Handle Call Api
        CompletableFuture<List<Double>> futureWeather = getWeatherCoordinatesAsync();
        CompletableFuture<List<Double>> futureLight = getLightCoordinatesAsync();
        CompletableFuture<MapDefault> futureMap = getMapAsync();

        // Set map controller
        IMapController mapController = map.getController();
        futureMap.thenAccept(mapDefault -> {
            mapController.setZoom(mapDefault.getZoom());
            GeoPoint startPoint = new GeoPoint(mapDefault.getCenter().get(1), mapDefault.getCenter().get(0));
            mapController.setCenter(startPoint);
            map.setMaxZoomLevel(mapDefault.getMaxZoom());
            map.setMinZoomLevel(mapDefault.getMinZoom());
        }).exceptionally(ex -> {
            Log.e(TAG, "Failed to retrieve map coordinates: " + ex.getMessage());
            return null;
        });

        // Retrieve coordinates of default weather marker
        futureWeather.thenAccept(this::handleWeatherCoordinates).exceptionally(ex -> {
            Log.e(TAG, "Failed to retrieve weather marker coordinates: " + ex.getMessage());
            return null;
        });

        // Retrieve coordinates of light marker
        futureLight.thenAccept(this::handleLightCoordinates).exceptionally(ex -> {
            Log.e(TAG, "Failed to retrieve light marker coordinates: " + ex.getMessage());
            return null;
        });

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.zoomIn();
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.zoomOut();
            }
        });

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(context, shrPrefs);
        map.onResume(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(context, shrPrefs);
        map.onPause();  // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults)
    {
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));

        if (permissionsToRequest.size() > 0)
        {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions)
    {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
            {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.size() > 0)
        {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private CompletableFuture<List<Double>> getWeatherCoordinatesAsync() {
        CompletableFuture<List<Double>> future = new CompletableFuture<>();
        Call<Asset> call = assetService.getAssetAttributes("Bearer " + token, getResources().getString(R.string.defaultWeatherAssetID));
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    Asset asset = response.body();
                    if (asset != null) {
                        Attribute attributes = asset.getAttributes();
                        if (attributes != null) {
                            Location location = attributes.getLocation();
                            if (location != null && location.getValue() != null) {
                                List<Double> defaultWeatherCoordinates = location.getValue().getCoordinates();
                                if (defaultWeatherCoordinates != null && defaultWeatherCoordinates.size() >= 2) {
                                    future.complete(defaultWeatherCoordinates);
                                } else {
                                    future.completeExceptionally(new RuntimeException("Default weather coordinates are null or insufficient"));
                                }
                            } else {
                                future.completeExceptionally(new RuntimeException("Location or its value is null"));
                            }
                        } else {
                            future.completeExceptionally(new RuntimeException("Attributes are null"));
                        }
                    } else {
                        future.completeExceptionally(new RuntimeException("Asset is null"));
                    }
                } else {
                    future.completeExceptionally(new RuntimeException("Response not successful: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    private void handleWeatherCoordinates(List<Double> coordinates) {
        // Use the coordinates as needed for marker creation or any other actions
        GeoPoint weatherMarkerPoint = new GeoPoint(coordinates.get(1), coordinates.get(0));
        Marker weatherMarker = new Marker(map);
        weatherMarker.setPosition(weatherMarkerPoint);
        weatherMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(weatherMarker);

        // Rest of your code related to markers and handling
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        int width = (int) (44 * displayMetrics.density);
        int height = (int) (44 * displayMetrics.density);

        Bitmap markerBitmapWeather = BitmapFactory.decodeResource(res, R.drawable.weather_marker);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(markerBitmapWeather, width, height, false);
        BitmapDrawable markerDrawableWeather = new BitmapDrawable(res, resizedBitmap);
        weatherMarker.setIcon(markerDrawableWeather);

        weatherMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                MapWeatherDialog dialog = new MapWeatherDialog();
                dialog.show(getParentFragmentManager(), "Map Weather Dialog");
                return true;
            }
        });
    }
    private CompletableFuture<List<Double>> getLightCoordinatesAsync() {
        CompletableFuture<List<Double>> future = new CompletableFuture<>();
        Call<Asset> call = assetService.getAssetAttributes("Bearer " + token, getResources().getString(R.string.lightAssetID));
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    Asset asset = response.body();
                    if (asset != null) {
                        Attribute attributes = asset.getAttributes();
                        if (attributes != null) {
                            Location location = attributes.getLocation();
                            if (location != null && location.getValue() != null) {
                                List<Double> defaultWeatherCoordinates = location.getValue().getCoordinates();
                                if (defaultWeatherCoordinates != null && defaultWeatherCoordinates.size() >= 2) {
                                    future.complete(defaultWeatherCoordinates);
                                } else {
                                    future.completeExceptionally(new RuntimeException("Default weather coordinates are null or insufficient"));
                                }
                            } else {
                                future.completeExceptionally(new RuntimeException("Location or its value is null"));
                            }
                        } else {
                            future.completeExceptionally(new RuntimeException("Attributes are null"));
                        }
                    } else {
                        future.completeExceptionally(new RuntimeException("Asset is null"));
                    }
                } else {
                    future.completeExceptionally(new RuntimeException("Response not successful: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    private void handleLightCoordinates(List<Double> coordinates) {
        // Use the coordinates as needed for marker creation or any other actions
        GeoPoint lightMarkerPoint = new GeoPoint(coordinates.get(1), coordinates.get(0));
        Marker lightMarker = new Marker(map);
        lightMarker.setPosition(lightMarkerPoint);
        lightMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(lightMarker);

        // Rest of your code related to markers and handling
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        int width = (int) (44 * displayMetrics.density);
        int height = (int) (44 * displayMetrics.density);

        Bitmap markerBitmapLight = BitmapFactory.decodeResource(res, R.drawable.lightbulb_marker);
        Bitmap resizedBitmapLight = Bitmap.createScaledBitmap(markerBitmapLight, width, height,false);
        BitmapDrawable markerDrawableLight = new BitmapDrawable(res, resizedBitmapLight);
        lightMarker.setIcon(markerDrawableLight);

        lightMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                MapLightDialog dialog = new MapLightDialog();
                dialog.show(getParentFragmentManager(), "Map Light Dialog");
                return true;
            }
        });
    }

    private CompletableFuture<MapDefault> getMapAsync() {
        CompletableFuture<MapDefault> future = new CompletableFuture<>();
        Call<MapAsset> call = assetService.getMapOptions("Bearer " + token);
        call.enqueue(new Callback<MapAsset>() {
            @Override
            public void onResponse(Call<MapAsset> call, Response<MapAsset> response) {
                MapAsset asset = response.body();
                MapOption options = asset.getOptions();
                MapDefault mapDefault = options.getMapDefault();

                if (mapDefault != null)
                {
                    future.complete(mapDefault);
                }
            }

            @Override
            public void onFailure(Call<MapAsset> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }
}