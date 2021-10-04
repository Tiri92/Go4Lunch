package com.go4lunch.ui.home.chat;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.go4lunch.di.DI;
import com.go4lunch.model.firestore.Message;
import com.go4lunch.repositories.ChatRepository;
import com.google.firebase.firestore.Query;

public class ChatActivityViewModel extends ViewModel {

    public FirestoreRecyclerOptions<Message> getPrivateChatMessage(String from, String to) {
        return ChatRepository.getPrivateChatRoomMessage(from, to);
    }

    /**
     * GET
     **/
    public String getCurrentUserId() {
        return ChatRepository.getCurrentUserId();
    }

    public String getCurrentUserUrlPic() {
        return ChatRepository.getCurrentUserUrlPic();
    }

    /**
     * INSERT
     **/
    public static void newMessage(Message newMessage) {
        ChatRepository.newMessage(newMessage);
    }

}
