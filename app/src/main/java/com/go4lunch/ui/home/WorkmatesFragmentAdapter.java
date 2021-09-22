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
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

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
        username.setText(listOfUsers.get(position).getUsername());

        ImageView userPic = itemView.findViewById(R.id.user_pic);
        try {
            String query = listOfUsers.get(position).getUrlPicture();

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
