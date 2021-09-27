package com.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch.model.GooglePlaceService;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.nearbysearch.NearbySearch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceRepository {

    private final MutableLiveData<NearbySearch> nearbySearchResult = new MutableLiveData<>();

    public LiveData<NearbySearch> getNearbySearchResult() {
        return nearbySearchResult;
    }

    private final MutableLiveData<DetailSearch> detailSearchResult = new MutableLiveData<>();

    public LiveData<DetailSearch> getDetailSearchResult() {
        return detailSearchResult;
    }

    // Get a Retrofit instance and the related endpoints
    GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

    // Create the call on GooglePlace API
    public void callRestaurant(String position) {
        Call<NearbySearch> liveDataCall = googlePlaceService.getRestaurants(position);
        liveDataCall.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(Call<NearbySearch> call, Response<NearbySearch> response) {
                if (response.isSuccessful()) {
                    nearbySearchResult.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NearbySearch> call, Throwable t) {

            }
        });
    }

    public void callRestaurantDetail(String placeId) {
        Call<DetailSearch> liveDataCall = googlePlaceService.getRestaurantsDetails(placeId);
        liveDataCall.enqueue(new Callback<DetailSearch>() {
            @Override
            public void onResponse(Call<DetailSearch> call, Response<DetailSearch> response) {
                if (response.isSuccessful()) {
                    detailSearchResult.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DetailSearch> call, Throwable t) {

            }
        });
    }

}
