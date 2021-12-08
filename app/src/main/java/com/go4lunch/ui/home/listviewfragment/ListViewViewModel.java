package com.go4lunch.ui.home.listviewfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.NearbySearch;

import java.util.List;

public class ListViewViewModel extends ViewModel {

    public LiveData<NearbySearch> getNearbySearchResultFromVM() {
        return DI.getGooglePlaceRepository().getNearbySearchResult();
    }

    public LiveData<List<User>> getListOfUsersWhoChoseRestaurant() {
        return DI.getFirestoreRepository().getListOfUsersWhoChoseRestaurant();
    }

    public void callAutocompleteSearch(String position, String input) {
        DI.getGooglePlaceRepository().callAutocompleteResult(position, input);
    }

    public LiveData<List<DetailSearch>> getAutocompleteSearchResultFromVM() {
        return DI.getGooglePlaceRepository().getAutocompleteSearchResult();
    }


}
