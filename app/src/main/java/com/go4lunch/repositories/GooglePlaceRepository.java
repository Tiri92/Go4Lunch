package com.go4lunch.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch.model.NearbySearchService;
import com.go4lunch.model.nearbysearch.NearbySearch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class GooglePlaceRepository {

    private MutableLiveData<NearbySearch> nearbySearchResult = new MutableLiveData<>();

    public LiveData<NearbySearch> getNearbySearchResult() {
        return nearbySearchResult;
    }

    // Get a Retrofit instance and the related endpoints
    NearbySearchService nearbySearchService = NearbySearchService.retrofit.create(NearbySearchService.class);

    // Create the call on GooglePlace API
    public void callRestaurant(String position) {
        Call<NearbySearch> liveDataCall = nearbySearchService.getRestaurants(position);
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

}
