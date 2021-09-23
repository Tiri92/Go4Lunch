package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;

import java.util.List;

public class WorkmatesFragmentViewModel extends ViewModel {

    public LiveData<List<User>> getListOfUsers() {
        return DI.getFirestoreRepository().getListOfUsers();
    }

}
