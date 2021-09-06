package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.nearbysearch.NearbySearch;
import com.google.android.gms.maps.model.LatLng;

public class MapViewViewModel extends ViewModel {

public void callNearbySearch(String position) { DI.getGooglePlaceRepository().callRestaurant(position); }

public LiveData<NearbySearch> getNearbySearchResultFromVM() {
   return DI.getGooglePlaceRepository().getNearbySearchResult();
}

}
