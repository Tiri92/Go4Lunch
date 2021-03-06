package com.go4lunch.ui.home.restaurantdetailactivity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;
import com.go4lunch.ui.home.chat.ChatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantDetailAdapter extends RecyclerView.Adapter<RestaurantDetailAdapter.ViewHolder> {

    View itemView;
    private final List<User> listOfUsersWhoChoseRestaurant;

    public RestaurantDetailAdapter(List<User> listOfUsersWhoChoseRestaurant) {
        this.listOfUsersWhoChoseRestaurant = listOfUsersWhoChoseRestaurant;
    }

    @NonNull
    @NotNull
    @Override
    public RestaurantDetailAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new RestaurantDetailAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RestaurantDetailAdapter.ViewHolder holder, int position) {

        String space = " ";
        TextView isJoiningTextView = new TextView(itemView.getContext());
        isJoiningTextView.setText(R.string.is_joining);
        holder.username.setText(String.format("%s%s%s", listOfUsersWhoChoseRestaurant.get(position).getUsername(), space, isJoiningTextView.getText().toString()));

        try {
            String query = listOfUsersWhoChoseRestaurant.get(position).getUrlPicture();

            Glide.with(holder.userPic)
                    .load(query)
                    .circleCrop()
                    .into(holder.userPic);

        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }

        holder.messageButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("userId", listOfUsersWhoChoseRestaurant.get(holder.getBindingAdapterPosition()).getUid());
            intent.putExtra("name", listOfUsersWhoChoseRestaurant.get(holder.getBindingAdapterPosition()).getUsername());
            ActivityCompat.startActivity(v.getContext(), intent, null);
        });

    }

    @Override
    public int getItemCount() {
        return listOfUsersWhoChoseRestaurant.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userPic;
        ImageButton messageButton;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            userPic = itemView.findViewById(R.id.user_pic);
            messageButton = itemView.findViewById(R.id.message_button);
        }
    }


}
