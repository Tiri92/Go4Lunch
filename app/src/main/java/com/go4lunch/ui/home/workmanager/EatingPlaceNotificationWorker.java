package com.go4lunch.ui.home.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.go4lunch.R;
import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EatingPlaceNotificationWorker extends Worker {

    public static final String KEY_EATING_PLACE = "KEY_EATING_PLACE";
    public static final String KEY_EATING_PLACE_ID = "KEY_EATING_PLACE_ID";
    public static final String KEY_EATING_PLACE_ADDRESS = "KEY_EATING_PLACE_ADDRESS";
    public static final String KEY_NOTIFICATION_TITLE = "KEY_NOTIFICATION_TITLE";
    public static final String KEY_NOTIFICATION_MESSAGE = "KEY_NOTIFICATION_MESSAGE";
    public static final String KEY_NOTIFICATION_MESSAGE_JOIN = "KEY_NOTIFICATION_MESSAGE_JOIN";
    public static final String USER_NAME = "USER_NAME";
    String eatingPlaceIdFromDb;
    private final Context context;

    public EatingPlaceNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String eatingPlace = data.getString(KEY_EATING_PLACE);
        String userName = data.getString(USER_NAME);
        String eatingPlaceId = data.getString(KEY_EATING_PLACE_ID);
        String eatingPlaceAddress = data.getString(KEY_EATING_PLACE_ADDRESS);
        String joiningMessage = data.getString(KEY_NOTIFICATION_MESSAGE_JOIN);
        String title = data.getString(KEY_NOTIFICATION_TITLE);
        String message = data.getString(KEY_NOTIFICATION_MESSAGE);
        if (!eatingPlace.equals(" ")) {
            DI.getFirestoreRepository().getUserData().addOnSuccessListener(documentSnapshot -> {
                eatingPlaceIdFromDb = documentSnapshot.get("eatingPlaceId").toString();
                if (!eatingPlaceIdFromDb.equals(" ")) {
                    DI.getFirestoreRepository().getUsersCollection().whereEqualTo("eatingPlaceId", eatingPlaceId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<User> users = task.getResult().toObjects(User.class);
                            if (users.size() > 1) {
                                String userList = "";
                                for (User user : users) {
                                    if (!user.getUsername().equals(userName)) {
                                        userList += " " + user.getUsername() + ",";
                                    }
                                }
                                displayNotification(title, message + " " + eatingPlace + "\n" + eatingPlaceAddress + "\n" + joiningMessage + "\n" + removeLastChar(userList));
                            } else {
                                displayNotification(title, message + " " + eatingPlace + "\n" + eatingPlaceAddress);
                            }
                        }
                    });

                    cleanEatingPlaceWorker(context);
                }
            });
        }
        return Result.success();
    }

    private void displayNotification(String task, String desc) {
        NotificationManager manager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("go4Lunch", "go4Lunch", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "go4Lunch")
                .setContentTitle(task)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher);

        manager.notify(1, builder.build());
    }

    private void cleanEatingPlaceWorker(Context context) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ClearEatingPlaceWorker.class)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork("clear", ExistingWorkPolicy.REPLACE, workRequest);
    }

    public String removeLastChar(String s) {
        return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
    }


}


