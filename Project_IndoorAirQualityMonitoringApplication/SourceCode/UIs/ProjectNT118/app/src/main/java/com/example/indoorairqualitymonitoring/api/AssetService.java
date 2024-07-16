package com.example.indoorairqualitymonitoring.api;

import com.example.indoorairqualitymonitoring.model.mapmarker.Asset;
import com.example.indoorairqualitymonitoring.model.datapoint.AssetDatapointAttributeObject;
import com.example.indoorairqualitymonitoring.model.map.MapAsset;
import com.example.indoorairqualitymonitoring.model.openweathermap.WeatherFeatures;
import com.example.indoorairqualitymonitoring.model.User;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AssetService
{
    // Call data from line chart on web
    @POST("api/master/asset/datapoint/{assetId}/attribute/{attributeName}")
    Call<JsonArray> getAssetDatapointAttribute(@Header("Authorization") String bearerToken,
                                               @Path("assetId") String assetID,
                                               @Path("attributeName") String attributeName,
                                               @Body AssetDatapointAttributeObject rawBody);

    // Call weather features through https://api.openweathermap.org/data/2.5/
    @GET("weather")
    Call<WeatherFeatures> getFeatures(@Query("appid") String appid,
                                      @Query("lon") double lon,
                                      @Query("units") String units,
                                      @Query("lat") double lat);

    // Call user's details
    @GET("api/master/user/user")
    Call<User> getUser(@Header("Authorization") String bearerToken);

    // Call assets
    @GET("api/master/asset/{assetId}")
    Call<Asset> getAssetAttributes(@Header("Authorization") String token,
                                   @Path("assetId") String assetId);

    // Call options of map
    @GET("api/master/map")
    Call<MapAsset> getMapOptions(@Header("Authorization") String token);
}