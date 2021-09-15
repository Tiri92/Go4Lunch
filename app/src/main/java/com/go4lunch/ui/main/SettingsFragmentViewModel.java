package com.go4lunch.ui.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragmentViewModel extends ViewModel {

    public FirebaseUser getCurrentUser() {
        return DI.getFirestoreRepository().getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return DI.getFirestoreRepository().signOut(context);
    }

    public void createUser() {
        //User userToCreate = new User();
        DI.getFirestoreRepository().createUser();
    }

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public Task<Void> updateUsername(String username) {
        return DI.getFirestoreRepository().updateUsername(username);
    }

    public Task<Void> deleteUser(Context context) {
        // Delete the user data from Firestore
        return DI.getFirestoreRepository().deleteUserFromFirestore(getCurrentUser().getUid()).addOnCompleteListener(task -> {
            // Once done, delete the user account from the Auth
            DI.getFirestoreRepository().deleteUser(context);
        });

    }
}
