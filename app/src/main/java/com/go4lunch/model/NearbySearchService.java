package com.go4lunch.model;

import androidx.lifecycle.LiveData;

import com.go4lunch.BuildConfig;
import com.go4lunch.model.nearbysearch.NearbySearch;
import com.go4lunch.model.nearbysearch.ResultsItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.jar.Manifest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbySearchService {

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("nearbysearch/json?radius=1500&type=restaurant&key="+BuildConfig.MAPS_API_KEY)
    Call<NearbySearch> getRestaurants(@Query("location") String position);

}
