package com.go4lunch.ui;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityViewModel extends ViewModel {

    public FirebaseUser getCurrentUser() {
        return DI.getFirestoreRepository().getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public void createUser() {
        //User userToCreate = new User();
        DI.getFirestoreRepository().createUser();
    }


}
