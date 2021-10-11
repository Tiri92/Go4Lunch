package com.go4lunch.repositories;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FirestoreRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String EATING_PLACE_ID = "eatingPlaceId";
    private static final String EATING_PLACE = "eatingPlace";
    private static final String LIST_OF_RESTAURANTS_LIKED = "listOfRestaurantsLiked";
    private static final String URL_PICTURE = "urlPicture";
    private final MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> listOfUsersWhoChoseRestaurant = new MutableLiveData<>();

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
        String urlDefaultPicture = "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_1280.png";
        if (user != null) {
            String uid = user.getUid();
            String username = user.getDisplayName();
            String email = (this.getCurrentUser().getEmail());
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : urlDefaultPicture;
            String eatingPlace = " ";
            String eatingPlaceId = " ";
            List<String> listOfRestaurantsLiked = new ArrayList<>();

            User userToCreate = new User(uid, username, email, urlPicture, eatingPlace, eatingPlaceId, listOfRestaurantsLiked);

            Task<DocumentSnapshot> userData = getUserData();

            userData.addOnFailureListener(documentSnapshot -> this.getUsersCollection().document(uid).set(userToCreate)).addOnSuccessListener(documentSnapshot -> {
                User user1 = documentSnapshot.toObject(User.class);
                if (user1 == null) {
                    getUsersCollection().document(uid).set(userToCreate);
                }
            });
        }
    }

    // Get All Users
    public void getAllUsers() {
        getUsersCollection()
                .orderBy("eatingPlace", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<User> allWorkMates = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot document : value.getDocuments()) {

                            User myUser = document.toObject(User.class);

                            allWorkMates.add(myUser);
                        }
                    }
                    listOfUsers.setValue(allWorkMates);
                });
    }

    public LiveData<List<User>> getListOfUsers() {
        return listOfUsers;
    }

    // Get Users who chose an eatingPlace
    public void getUsersWhoChoseRestaurant() {
        getUsersCollection()
                .whereNotEqualTo("eatingPlace", " ")
                .addSnapshotListener((value, error) -> {
                    List<User> allWorkMatesWhoChoseRestaurant = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                            User myUser = documentSnapshot.toObject(User.class);

                            allWorkMatesWhoChoseRestaurant.add(myUser);
                        }
                    }
                    listOfUsersWhoChoseRestaurant.setValue(allWorkMatesWhoChoseRestaurant);
                });
    }

    public LiveData<List<User>> getListOfUsersWhoChoseRestaurant() {
        getUsersWhoChoseRestaurant();
        return listOfUsersWhoChoseRestaurant;
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

    // Update Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // Update UrlPicture
    public Task<Void> updateUrlPicture(String urlPicture) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(URL_PICTURE, urlPicture);
        } else {
            return null;
        }
    }

    // Upload UrlPicture TODO Understand look Denis Answer in email, and look the lesson about Firestore Storage OC
    public void uploadPhotoInFirebaseAndUpdateUrlPicture(Uri uri) {
        String uuid = UUID.randomUUID().toString();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        UploadTask uploadTask = mImageRef.putFile(uri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return mImageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                updateUrlPicture(downloadUri.toString());
            }
        });
    }

    // Update EatingPlaceId
    public Task<Void> updateEatingPlaceId(String eatingPlaceId) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(EATING_PLACE_ID, eatingPlaceId);
        } else {
            return null;
        }
    }

    // Update EatingPlace
    public Task<Void> updateEatingPlace(String eatingPlace) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(EATING_PLACE, eatingPlace);
        } else {
            return null;
        }
    }

    /**
     * *** Update EatingPlace After Notification  ****
     **/
    private Task<Void> updateEatingPlaceNameAN(String uid, String eatingPlaceName) {
        return getUsersCollection().document(uid).update("eatingPlace", eatingPlaceName);
    }

    private Task<Void> updateEatingPlaceIdAN(String uid, String eatingPlaceId) {
        return getUsersCollection().document(uid).update("eatingPlaceId", eatingPlaceId);
    }

    public void updateEatingPlaceAN(String uId, String eatingPlaceName, String eatingPlaceId) {
        this.updateEatingPlaceNameAN(uId, eatingPlaceName);
        this.updateEatingPlaceIdAN(uId, eatingPlaceId);
    }

    /**
     * ****************************
     **/

    // Update ListOfRestaurantsLiked
    public Task<Void> updateListOfRestaurantsLiked(List<String> listOfRestaurantsLiked) {
        String uid = this.getCurrentUserId();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(LIST_OF_RESTAURANTS_LIKED, listOfRestaurantsLiked);
        } else {
            return null;
        }
    }

    // Delete the User from Firestore
    public Task<Void> deleteUserFromFirestore(String userId) {
        return getUsersCollection().document(userId).delete();
    }


}
