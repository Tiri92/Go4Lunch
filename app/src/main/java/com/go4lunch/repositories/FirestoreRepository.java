package com.go4lunch.repositories;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";

    // Get the Collection Reference
    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getCurrentUserId() {
        return getCurrentUser().getUid();
    }

    public Task<Void> logout(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    /**
     * Firestore Request, CRUD action
     **/

    // Create User in Firestore TODO To understand
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String username = user.getDisplayName();
            String email = (this.getCurrentUser().getEmail());
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            User userToCreate = new User(uid, username, email, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data
            userData.addOnFailureListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) {
                        getUsersCollection().document(uid).set(userToCreate);
                    }
                }
            });
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public DocumentReference getUserDataForUpdate() {
        return getUsersCollection().document(getCurrentUserId());
    }

    // Update User Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // Delete the User from Firestore
    public Task<Void> deleteUserFromFirestore(String userId) {
        return getUsersCollection().document(userId).delete();
    }

}
