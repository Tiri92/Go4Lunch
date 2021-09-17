package com.go4lunch.ui.home;

import android.content.Intent;
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
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.model.nearbysearch.ResultsItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListViewFragmentAdapter extends RecyclerView.Adapter<ListViewFragmentAdapter.ViewHolder> {

    View itemView;
    private List<ResultsItem> listOfRestaurants;

    public ListViewFragmentAdapter(List<ResultsItem> listOfRestaurants) {
        this.listOfRestaurants = listOfRestaurants;
    }

    @NonNull
    @NotNull
    @Override
    public ListViewFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListViewFragmentAdapter.ViewHolder holder, int position) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("placeId", listOfRestaurants.get(position).getPlaceId());
                ActivityCompat.startActivity(v.getContext(), intent, null);
            }
        });

        TextView nameOfRestaurant = itemView.findViewById(R.id.restaurant_name);
        nameOfRestaurant.setText(listOfRestaurants.get(position).getName());

        TextView restaurantAddress = itemView.findViewById(R.id.restaurant_address);
        restaurantAddress.setText(listOfRestaurants.get(position).getVicinity());

        TextView openingHour = itemView.findViewById(R.id.restaurant_opening_hours);
        if (listOfRestaurants.get(position).getOpeningHours() != null) {
            if (listOfRestaurants.get(position).getOpeningHours().isOpenNow()) {
                openingHour.setText(R.string.Open_now);
            } else {
                openingHour.setText(R.string.Close_now);
            }
        } else {
            openingHour.setText(R.string.We_dont_know);
        }

        ImageView restaurantPic = itemView.findViewById(R.id.restaurant_pic);
        try {
            String base = "https://maps.googleapis.com/maps/api/place/photo?";
            String key = "key=" + BuildConfig.MAPS_API_KEY;
            String reference = "&photoreference=" + listOfRestaurants.get(position).getPhotos().get(0).getPhotoReference();
            String maxH = "&maxheight=157";
            String maxW = "&maxwidth=157";
            String query = base + key + reference + maxH + maxW;

            Glide.with(restaurantPic)
                    .load(query)
                    .centerCrop()
                    .into(restaurantPic);

        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listOfRestaurants.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
