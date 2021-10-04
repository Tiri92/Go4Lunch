package com.go4lunch.ui.home.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import java.util.Arrays;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnDataChange {

    private ActivityChatBinding binding;
    private ChatAdapter mAdapter;
    private ChatActivityViewModel chatActivityViewModel;
    private String userId;
    private String userName;
    private String userPicUrl;
    private String currentUserId;
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USER_NAME = "name";
    public static final String EXTRA_USER_PIC = "userPic";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        chatActivityViewModel = new ViewModelProvider(this).get(ChatActivityViewModel.class);


        configureToolbar();
        currentUserId = chatActivityViewModel.getCurrentUserId();
        configureRecyclerView();
        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);
        userName = intent.getStringExtra(EXTRA_USER_NAME);
        userPicUrl = intent.getStringExtra(EXTRA_USER_PIC);

        binding.sendBtn.setOnClickListener(v -> {
            if (!binding.editTextMessage.getText().toString().isEmpty()) {
                Date date = new Date();
                Message message = new Message(currentUserId, userId, binding.editTextMessage.getText().toString(), userPicUrl,
                        date, Arrays.asList(currentUserId, userId));
                ChatActivityViewModel.newMessage(message);
                binding.editTextMessage.setText("");
            }
        });
    }

    /**
     * For return button
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        setSupportActionBar(binding.toolbar);
        TextView mTitle = (TextView) binding.toolbar.findViewById(R.id.pseudo_name);
        mTitle.setText(userName);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
