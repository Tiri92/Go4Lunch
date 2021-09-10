package com.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch.model.GooglePlaceService;
import com.go4lunch.model.details.SearchDetail;
import com.go4lunch.model.nearbysearch.NearbySearch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceRepository {

    private MutableLiveData<NearbySearch> nearbySearchResult = new MutableLiveData<>();

    public LiveData<NearbySearch> getNearbySearchResult() {
        return nearbySearchResult;
    }

    private MutableLiveData<SearchDetail> detailResult = new MutableLiveData<>();

    public LiveData<SearchDetail> getDetailSearchResult() {
        return detailResult;
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

    public void callDetailRestaurant(String placeId) {
        Call<SearchDetail> liveDataCall = googlePlaceService.getRestaurantsDetails(placeId);
        liveDataCall.enqueue(new Callback<SearchDetail>() {
            @Override
            public void onResponse(Call<SearchDetail> call, Response<SearchDetail> response) {
                if (response.isSuccessful()) {
                    detailResult.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SearchDetail> call, Throwable t) {

            }
        });
    }

}
