package com.go4lunch.repositories;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.go4lunch.model.firestore.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatRepository {

    private static final String COLLECTION_NAME = "message";
    private static final String COLLECTION_NAME_USERS = "users";
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static CollectionReference getMessageCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_USERS);
    }

    //Get message from FirestoreRecyclerOptions
    public static FirestoreRecyclerOptions<Message> getPrivateChatRoomMessage(String from, String to) {
        Query query = getMessageCollection().whereIn("between", Arrays.asList(Arrays.asList(from, to), Arrays.asList(to, from))).orderBy("date", Query.Direction.ASCENDING);
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
    }

    public static String getCurrentUserId() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = getCurrentUserId();
        return this.getUsersCollection().document(uid).get();
    }

    //Insert message in firestore
    public static Task<Void> newMessage(Message newMessage) {
        return getMessageCollection().document().set(newMessage);
    }


}
