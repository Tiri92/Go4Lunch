package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.details.SearchDetail;
import com.go4lunch.model.nearbysearch.NearbySearch;

public class RestaurantDetailViewModel extends ViewModel {

    public void callRestaurantDetail(String placeId) {
        DI.getGooglePlaceRepository().callRestaurantDetail(placeId);
    }

    public LiveData<SearchDetail> getSearchDetailResultFromVM() {
        return DI.getGooglePlaceRepository().getSearchDetailResult();
    }

}
