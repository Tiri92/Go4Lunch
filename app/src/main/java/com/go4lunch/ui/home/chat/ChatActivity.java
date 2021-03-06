package com.go4lunch.ui.home.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.go4lunch.R;
import com.go4lunch.databinding.ActivityChatBinding;
import com.go4lunch.model.firestore.Message;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnDataChange {

    private ActivityChatBinding binding;
    private ChatActivityViewModel chatActivityViewModel;
    private ChatAdapter mAdapter;
    private String currentUserId;
    private String currentUserPicUrl;
    private String userId;
    private String userName;
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USER_NAME = "name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        chatActivityViewModel = new ViewModelProvider(this).get(ChatActivityViewModel.class);
        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);
        userName = intent.getStringExtra(EXTRA_USER_NAME);
        currentUserId = chatActivityViewModel.getCurrentUserId();
        chatActivityViewModel.getUserData().addOnSuccessListener(user -> currentUserPicUrl = user.getUrlPicture());

        configureRecyclerView();
        configureToolbar();

        binding.sendBtn.setOnClickListener(v -> {
            if (!binding.editTextMessage.getText().toString().isEmpty()) {
                Date date = new Date();
                Message message = new Message(currentUserId, userId, binding.editTextMessage.getText().toString(), currentUserPicUrl,
                        date, Arrays.asList(currentUserId, userId));
                ChatActivityViewModel.newMessage(message);
                binding.editTextMessage.setText("");
            }
        });

    }

    private void configureToolbar() {
        String space = " ";
        setSupportActionBar(binding.toolbar);
        TextView mTitle = binding.toolbar.findViewById(R.id.pseudo_name);
        mTitle.setText(MessageFormat.format("{0}{1}{2}", mTitle.getText().toString(), space, userName));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
    }

    /**
     * Configure RecyclerView
     **/
    private void configureRecyclerView() {
        binding.messageRecyclerView.setHasFixedSize(true);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatAdapter(chatActivityViewModel.getPrivateChatMessage(currentUserId, userId), currentUserId, this);
        binding.messageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        binding.messageRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDataChanged() {
        binding.messageRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }


}
