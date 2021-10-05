package com.go4lunch.ui.home.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.go4lunch.R;
import com.go4lunch.databinding.MessageItemBinding;
import com.go4lunch.model.firestore.Message;

import java.text.DateFormat;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> {

    public interface OnDataChange {
        void onDataChanged();
    }

    private LinearLayout profileContainer;
    private RelativeLayout rootView;
    private RelativeLayout messageContainer;
    private final String currentUserId;
    private final OnDataChange onDataChange;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, String currentUserId, OnDataChange onDataChange) {
        super(options);
        this.currentUserId = currentUserId;
        this.onDataChange = onDataChange;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Message model) {
        messageContainer = holder.binding.activityChatItemMessageContainer;
        rootView = holder.binding.activityMentorChatItemRootView;
        profileContainer = holder.binding.activityChatItemProfileContainer;

        holder.binding.activityChatItemMessageContainerTextMessageContainerTextView.setText(model.getMessage());
        holder.binding.activityChatItemMessageContainerTextViewDate.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(model.getDate()));

        updateDesignDependingUser(model.getFrom().equals(currentUserId));

        Glide.with(holder.binding.activityChatItemProfileContainerProfileImage.getContext())
                .load(model.getUrlPicFrom())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.binding.activityChatItemProfileContainerProfileImage);
    }

    private void updateDesignDependingUser(Boolean isSender) { //TODO Understand how it work

        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.activity_chat_item_profile_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        this.rootView.requestLayout();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        onDataChange.onDataChanged();
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ChatHolder(MessageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        MessageItemBinding binding;

        public ChatHolder(MessageItemBinding messageItemBinding) {
            super(messageItemBinding.getRoot());
            binding = messageItemBinding;
        }
    }
}
