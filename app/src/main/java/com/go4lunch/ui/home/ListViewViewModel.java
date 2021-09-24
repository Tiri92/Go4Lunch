package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
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

}
