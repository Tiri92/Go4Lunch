package com.go4lunch.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.go4lunch.model.GooglePlaceService;
import com.go4lunch.model.autocomplete.AutocompleteSearch;
import com.go4lunch.model.autocomplete.PredictionsResultItem;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.nearbysearch.NearbySearch;

import java.util.ArrayList;
import java.util.List;

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

    private final MutableLiveData<List<DetailSearch>> autocompleteSearchResult = new MutableLiveData<>();

    public LiveData<List<DetailSearch>> getAutocompleteSearchResult() {
        return autocompleteSearchResult;
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

    /**
     * We start Autocomplete request and after we start a loop that iterates over each PredictionResultItem (5 restaurants) and we took their placeId for directly start
     * DetailRequest in which thanks to a List of DetailSearch we will add the result of Detail request on the restaurant of Autocomplete on which we iterate, and to finish
     * we set our LiveData autocompleteSearchResult who get result of Autocomplete request with last List of DetailSearch
     */
    public void callAutocompleteResult(String position, String input) {
        Call<AutocompleteSearch> liveDataCall = googlePlaceService.getAutocompleteResult(position, input);
        liveDataCall.enqueue(new Callback<AutocompleteSearch>() {
            @Override
            public void onResponse(Call<AutocompleteSearch> call, Response<AutocompleteSearch> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DetailSearch> detailSearchList = new ArrayList<>();
                    for (PredictionsResultItem prediction : response.body().getPredictions()) {
                        Call<DetailSearch> liveDataCall = googlePlaceService.getRestaurantsDetails(prediction.getPlaceId());
                        liveDataCall.enqueue(new Callback<DetailSearch>() {
                            @Override
                            public void onResponse(Call<DetailSearch> call, Response<DetailSearch> response) {
                                if (response.isSuccessful()) {
                                    detailSearchList.add(response.body());
                                    autocompleteSearchResult.setValue(detailSearchList);
                                }
                            }

                            @Override
                            public void onFailure(Call<DetailSearch> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<AutocompleteSearch> call, Throwable t) {

            }
        });
    }


}
