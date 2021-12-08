package com.go4lunch.ui.main.settingsfragment;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragmentViewModel extends ViewModel {

    public FirebaseUser getCurrentUser() {
        return DI.getFirestoreRepository().getCurrentUser();
    }

    /**
     * Firestore Request, CRUD action
     **/

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public Task<Void> updateUsername(String username) {
        return DI.getFirestoreRepository().updateUsername(username);
    }

    public void updateUrlPicture(Uri urlPicture) {
        DI.getFirestoreRepository().uploadPhotoInFirebaseAndUpdateUrlPicture(urlPicture);
    }

    public Task<Void> deleteUser(Context context) {
        // Delete the user data from Firestore
        return DI.getFirestoreRepository().deleteUserFromFirestore(getCurrentUser().getUid()).addOnCompleteListener(task -> {
            // Once done, delete the user account from the Auth
            DI.getFirestoreRepository().deleteUser(context);
        });
    }


}
