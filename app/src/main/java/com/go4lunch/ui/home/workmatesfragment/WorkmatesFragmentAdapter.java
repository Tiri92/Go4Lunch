package com.go4lunch.ui.home.workmatesfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;
import com.go4lunch.ui.home.chat.ChatActivity;
import com.go4lunch.ui.home.restaurantdetailactivity.RestaurantDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragmentAdapter extends RecyclerView.Adapter<WorkmatesFragmentAdapter.ViewHolder> implements Filterable {

    View itemView;
    private final List<User> listOfUsers;
    private final List<User> listOfUsersFull;

    public WorkmatesFragmentAdapter(List<User> listOfUsers, List<User> listOfUsersFull) {
        this.listOfUsers = listOfUsers;
        this.listOfUsersFull = listOfUsersFull;
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

        String space = " ";
        if (listOfUsers.get(position).getEatingPlaceId().equals(" ")) {
            TextView notDecidedTextView = new TextView(itemView.getContext());
            notDecidedTextView.setText(R.string.has_not_decided);
            holder.username.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            holder.username.setTextColor(Color.parseColor("#C6C6C6"));
            holder.username.setText(MessageFormat.format("{0}{1}{2}", listOfUsers.get(position).getUsername(), space, notDecidedTextView.getText().toString()));
        } else {
            TextView isEatingTextView = new TextView(itemView.getContext());
            isEatingTextView.setText(R.string.is_eating_in);
            holder.username.setText(MessageFormat.format("{0}{1}{2}{3}{4}", listOfUsers.get(position).getUsername(), space, isEatingTextView.getText().toString(), space, listOfUsers.get(position).getEatingPlace()));
            holder.username.setTextColor(Color.parseColor("#FF000000"));
            holder.username.setOnClickListener(v -> {
                if (!listOfUsers.get(holder.getBindingAdapterPosition()).getEatingPlaceId().equals(" ")) {
                    Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                    intent.putExtra("placeId", listOfUsers.get(holder.getBindingAdapterPosition()).getEatingPlaceId());
                    intent.putExtra("name", listOfUsers.get(holder.getBindingAdapterPosition()).getEatingPlace());
                    ActivityCompat.startActivity(v.getContext(), intent, null);
                }
            });
        }

        try {
            String query = listOfUsers.get(position).getUrlPicture();

            Glide.with(holder.userPic)
                    .load(query)
                    .circleCrop()
                    .into(holder.userPic);

        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }

        holder.messageButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("userId", listOfUsers.get(holder.getBindingAdapterPosition()).getUid());
            intent.putExtra("name", listOfUsers.get(holder.getBindingAdapterPosition()).getUsername());
            ActivityCompat.startActivity(v.getContext(), intent, null);
        });

    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }

    @Override
    public Filter getFilter() {
        return listOfUsersFilter;
    }

    private final Filter listOfUsersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = getFilterResults(constraint);
            Filter.FilterResults results = new Filter.FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listOfUsers.clear();
            listOfUsers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @NonNull
    @VisibleForTesting
    public List<User> getFilterResults(CharSequence constraint) {
        List<User> filteredList = new ArrayList<>();
        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(listOfUsersFull);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();

            for (User user : listOfUsersFull) {
                if (user.getUsername().toLowerCase().contains(filterPattern)) {
                    filteredList.add(user);
                }
            }
        }

        return filteredList;
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
