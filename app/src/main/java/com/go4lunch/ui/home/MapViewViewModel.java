package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.autocomplete.AutocompleteSearch;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.NearbySearch;

import java.util.List;

public class MapViewViewModel extends ViewModel {

    public void callNearbySearch(String position) {
        DI.getGooglePlaceRepository().callRestaurant(position);
    }

    public LiveData<NearbySearch> getNearbySearchResultFromVM() {
        return DI.getGooglePlaceRepository().getNearbySearchResult();
    }

    public void callAutocompleteSearch(String position, String input) {
        DI.getGooglePlaceRepository().callAutocompleteResult(position, input);
    }

    public LiveData<AutocompleteSearch> getAutocompleteSearchResultFromVM() {
        return DI.getGooglePlaceRepository().getAutocompleteSearchResult();
    }

    public void callRestaurantDetail(String placeId) {
        DI.getGooglePlaceRepository().callRestaurantDetail(placeId);
    }

    public LiveData<DetailSearch> getSearchDetailResultFromVM() {
        return DI.getGooglePlaceRepository().getDetailSearchResult();
    }

    public LiveData<List<User>> getListOfUsersWhoChoseRestaurant() {
        return DI.getFirestoreRepository().getListOfUsersWhoChoseRestaurant();
    }

}
