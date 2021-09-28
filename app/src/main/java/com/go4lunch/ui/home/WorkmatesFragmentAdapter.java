package com.go4lunch.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;

public class WorkmatesFragmentAdapter extends RecyclerView.Adapter<WorkmatesFragmentAdapter.ViewHolder> {

    View itemView;
    private final List<User> listOfUsers;

    public WorkmatesFragmentAdapter(List<User> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    @NonNull
    @NotNull
    @Override
    public WorkmatesFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new WorkmatesFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WorkmatesFragmentAdapter.ViewHolder holder, int position) {
        TextView username = itemView.findViewById(R.id.user_name);
        String space = " ";
        if (listOfUsers.get(holder.getAdapterPosition()).getEatingPlaceId().equals(" ")) {
            String notDecided = "hasn't decided yet";
            username.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            username.setTextColor(Color.parseColor("#C6C6C6"));
            username.setText(MessageFormat.format("{0}{1}{2}", listOfUsers.get(holder.getAdapterPosition()).getUsername(), space, notDecided));
        } else {
            String isEating = "is eating in";
            username.setText(MessageFormat.format("{0}{1}{2}{3}{4}", listOfUsers.get(holder.getAdapterPosition()).getUsername(), space, isEating, space, listOfUsers.get(position).getEatingPlace()));
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                    intent.putExtra("placeId", listOfUsers.get(holder.getAdapterPosition()).getEatingPlaceId());
                    ActivityCompat.startActivity(v.getContext(), intent, null);
                }
            });
        }

        ImageView userPic = itemView.findViewById(R.id.user_pic);
        try {
            String query = listOfUsers.get(holder.getAdapterPosition()).getUrlPicture();

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
        return listOfUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
