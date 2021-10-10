package com.go4lunch.ui.home.chat;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.Message;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.Task;

public class ChatActivityViewModel extends ViewModel {

    public FirestoreRecyclerOptions<Message> getPrivateChatMessage(String from, String to) {
        return DI.getChatRepository().getPrivateChatRoomMessage(from, to);
    }

    //GET
    public String getCurrentUserId() {
        return DI.getChatRepository().getCurrentUserId();
    }

    // Get the user from Firestore and cast it to a User model Object
    public Task<User> getUserData() {
        return DI.getFirestoreRepository().getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    //INSERT
    public static void newMessage(Message newMessage) {
        DI.getChatRepository().newMessage(newMessage);
    }


}
