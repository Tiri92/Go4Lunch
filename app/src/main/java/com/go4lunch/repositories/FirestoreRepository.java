package com.go4lunch.repositories;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirestoreRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private final MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>();

    public FirestoreRepository() {
        getAllUsers();
    }

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

    /* Create User in Firestore
       If user is authenticated, we try to get his data from Firestore with getUserData
       If getUserData fail, we create the user on Firebase
       If getUserData success but the user == null (doesn't exist in Firestore) we create it in Firebase
       And if the user of getUserData != null, so he already exist and we do nothing
    */
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String username = user.getDisplayName();
            String email = (this.getCurrentUser().getEmail());
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            User userToCreate = new User(uid, username, email, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();

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

    // Get All Users
    public void getAllUsers() {
        getUsersCollection().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> allWorkMates = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                User myUser = new User();
                                if (document.get("uid") != null) {
                                    myUser.setUid(document.get("uid").toString());
                                }

                                if (document.get("urlPicture") != null) {
                                    myUser.setUrlPicture(document.get("urlPicture").toString());
                                }

                                if (document.get("username") != null) {
                                    myUser.setUsername(document.get("username").toString());
                                }

                                allWorkMates.add(myUser);
                            }
                            listOfUsers.setValue(allWorkMates);
                        } else {
                            Log.e("FirestoreRepository", "method getAllUsers don't work" + task.getException());
                        }
                    }
                });
    }

    public LiveData<List<User>> getListOfUsers() {
        return listOfUsers;
    }

    // Update Username
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
