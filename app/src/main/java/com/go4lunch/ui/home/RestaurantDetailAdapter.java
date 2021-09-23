package com.go4lunch.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.R;
import com.go4lunch.model.User;

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

        TextView username = itemView.findViewById(R.id.user_name);
        username.setText(listOfUsersWhoChoseRestaurant.get(position).getUsername());

        ImageView userPic = itemView.findViewById(R.id.user_pic);
        try {
            String query = listOfUsersWhoChoseRestaurant.get(position).getUrlPicture();

            Glide.with(userPic)
                    .load(query)
                    .circleCrop()
                    .into(userPic);

        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return listOfUsersWhoChoseRestaurant.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
