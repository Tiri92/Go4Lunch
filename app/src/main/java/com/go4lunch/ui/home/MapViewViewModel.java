package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.nearbysearch.NearbySearch;

public class MapViewViewModel extends ViewModel {

    MutableLiveData<String> userPosition = new MutableLiveData<>();

    public void callNearbySearch(String position) {
        DI.getGooglePlaceRepository().callRestaurant(position);
    }

    public LiveData<NearbySearch> getNearbySearchResultFromVM() {
        return DI.getGooglePlaceRepository().getNearbySearchResult();
    }

    public LiveData<String> getUserPositionFromVM() {
        return userPosition;
    }

    public void savePosition(String s) {
        userPosition.postValue(s);
    }
}
