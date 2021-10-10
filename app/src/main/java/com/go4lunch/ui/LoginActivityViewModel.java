package com.go4lunch.ui;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;

public class LoginActivityViewModel extends ViewModel {

    public void createUser() {
        //User userToCreate = new User();
        DI.getFirestoreRepository().createUser();
    }


}
