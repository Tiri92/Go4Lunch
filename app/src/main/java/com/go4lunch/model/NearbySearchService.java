package com.go4lunch.model;

import com.go4lunch.BuildConfig;
import com.go4lunch.model.nearbysearch.NearbySearch;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbySearchService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("nearbysearch/json?radius=1500&type=restaurant&key=" + BuildConfig.MAPS_API_KEY)
    Call<NearbySearch> getRestaurants(@Query("location") String position);

}
