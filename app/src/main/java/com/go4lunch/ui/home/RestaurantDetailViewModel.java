package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.details.SearchDetail;

public class RestaurantDetailViewModel extends ViewModel {

    public void callDetailSearch(String placeId) {
        DI.getGooglePlaceRepository().callDetailRestaurant(placeId);
    }

    public LiveData<SearchDetail> getDetailSearchResultFromVM() {
        return DI.getGooglePlaceRepository().getDetailSearchResult();
    }

}
