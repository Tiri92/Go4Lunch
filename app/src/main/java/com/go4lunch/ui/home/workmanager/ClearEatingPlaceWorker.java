package com.go4lunch.ui.home.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClearEatingPlaceWorker extends Worker {

    public ClearEatingPlaceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        clearEatingPlace();
        return Result.success();
    }


    private void clearEatingPlace() {
        getUserCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (User user : task.getResult().toObjects(User.class)) {
                    updateEatingPlace(user.getUid(), " ", " ");
                }
            }
        });
    }

    private CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * *** Update EatingPlace  ****
     **/
    private Task<Void> updateEatingPlaceName(String uid, String eatingPlaceName) {
        return getUserCollection().document(uid).update("eatingPlace", eatingPlaceName);
    }

    private Task<Void> updateEatingPlaceId(String uid, String eatingPlaceId) {
        return getUserCollection().document(uid).update("eatingPlaceId", eatingPlaceId);
    }

    public void updateEatingPlace(String uId, String eatingPlaceName, String eatingPlaceId) {
        this.updateEatingPlaceName(uId, eatingPlaceName);
        this.updateEatingPlaceId(uId, eatingPlaceId);
    }
    /** ***************************** **/


}

