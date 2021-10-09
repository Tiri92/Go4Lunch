package com.go4lunch.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class WorkmatesFragmentViewModel extends ViewModel {

    public LiveData<List<User>> getListOfUsers() {
        return DI.getFirestoreRepository().getListOfUsers();
    }

    public Task<User> getUserData() {
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }


}
