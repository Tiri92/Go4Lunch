package com.go4lunch.model;

import com.go4lunch.BuildConfig;
import com.go4lunch.model.autocomplete.AutocompleteSearch;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.nearbysearch.NearbySearch;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlaceService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // For Restaurants search
    @GET("nearbysearch/json?radius=1500&type=restaurant&key=" + BuildConfig.MAPS_API_KEY)
    Call<NearbySearch> getRestaurants(@Query("location") String position);

    // For Restaurants details search
    @GET("details/json?fields=formatted_phone_number,place_id,geometry,url,rating,website,photo,opening_hours,vicinity,name&key=" + BuildConfig.MAPS_API_KEY)
    Call<DetailSearch> getRestaurantsDetails(@Query("place_id") String placeId);

    // For Autocomplete search
    @GET("autocomplete/json?types=establishment&radius=1500&language=fr&key=" + BuildConfig.MAPS_API_KEY)
    Call<AutocompleteSearch> getAutocompleteResult(@Query("location") String position, @Query("input") String input);

}
