package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.details.DetailSearch;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel {

    public void callRestaurantDetail(String placeId) {
        DI.getGooglePlaceRepository().callRestaurantDetail(placeId);
    }

    public LiveData<DetailSearch> getSearchDetailResultFromVM() {
        return DI.getGooglePlaceRepository().getSearchDetailResult();
    }

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public DocumentReference getUserDataForUpdate() {
        return DI.getFirestoreRepository().getUserDataForUpdate();
    }

    public Task<Void> updateEatingPlaceId(String eatingPlaceId) {
        return DI.getFirestoreRepository().updateEatingPlaceId(eatingPlaceId);
    }

    public Task<Void> updateEatingPlace(String eatingPlace) {
        return DI.getFirestoreRepository().updateEatingPlace(eatingPlace);
    }

    public Task<Void> updateListOfRestaurantsLiked(List<String> listOfRestaurantsLiked) {
        return DI.getFirestoreRepository().updateListOfRestaurantsLiked(listOfRestaurantsLiked);
    }

    public LiveData<List<User>> getListOfUsersWhoChoseRestaurant() {
        return DI.getFirestoreRepository().getListOfUsersWhoChoseRestaurant();
    }

}
