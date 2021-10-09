package com.go4lunch.ui.main;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;

public class LunchFragmentViewModel extends ViewModel {

    public Task<User> getUserData() {
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public void callRestaurantDetail(String placeId) {
        DI.getGooglePlaceRepository().callRestaurantDetail(placeId);
    }


}
