package com.go4lunch.ui;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class MainActivityViewModel extends ViewModel {

    public FirebaseUser getCurrentUser() {
        return DI.getFirestoreRepository().getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    // Get the user from Firestore and cast it to a User model Object
    public Task<User> getUserData() {
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public DocumentReference getUserDataForUpdate() {
        return DI.getFirestoreRepository().getUserDataForUpdate();
    }


}
