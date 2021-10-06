package com.go4lunch.ui.home.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.go4lunch.di.DI;
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
        DI.getFirestoreRepository().getUsersCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (User user : task.getResult().toObjects(User.class)) {
                    DI.getFirestoreRepository().updateEatingPlaceAN(user.getUid(), " ", " ");
                }
            }
        });
    }


}

