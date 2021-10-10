package com.go4lunch.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.ResultsItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;

public class ListViewFragmentAdapter extends RecyclerView.Adapter<ListViewFragmentAdapter.ViewHolder> {

    View itemView;
    private final List<ResultsItem> listOfRestaurants;
    private final List<User> listOfUserWhoChoseWhereLunch;

    public ListViewFragmentAdapter(List<ResultsItem> listOfRestaurants, List<User> listOfUserWhoChoseWhereLunch) {
        this.listOfRestaurants = listOfRestaurants;
        this.listOfUserWhoChoseWhereLunch = listOfUserWhoChoseWhereLunch;
    }

    @NonNull
    @NotNull
    @Override
    public ListViewFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ListViewFragmentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("placeId", listOfRestaurants.get(holder.getAdapterPosition()).getPlaceId());
            intent.putExtra("name", listOfRestaurants.get(holder.getAdapterPosition()).getName());
            intent.putExtra("address", listOfRestaurants.get(holder.getAdapterPosition()).getVicinity());
            ActivityCompat.startActivity(v.getContext(), intent, null);
        });

        holder.nameOfRestaurant.setText(listOfRestaurants.get(position).getName());

        holder.restaurantAddress.setText(listOfRestaurants.get(position).getVicinity());

        float getRatingOnThree = (float) (listOfRestaurants.get(position).getRating() / 1.66);
        holder.ratingBar.setRating(getRatingOnThree);

        if (listOfRestaurants.get(position).getOpeningHours() != null) {
            if (listOfRestaurants.get(position).getOpeningHours().isOpenNow()) {
                holder.openingHour.setText(R.string.Open_now);
            } else {
                holder.openingHour.setText(R.string.Close_now);
            }
        } else {
            holder.openingHour.setText(R.string.we_dont_know);
        }

        try {
            String base = "https://maps.googleapis.com/maps/api/place/photo?";
            String key = "key=" + BuildConfig.MAPS_API_KEY;
            String reference = "&photoreference=" + listOfRestaurants.get(position).getPhotos().get(0).getPhotoReference();
            String maxH = "&maxheight=157";
            String maxW = "&maxwidth=157";
            String query = base + key + reference + maxH + maxW;

            Glide.with(holder.restaurantPic)
                    .load(query)
                    .centerCrop()
                    .into(holder.restaurantPic);

        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }

        LatLng startLatLng = new LatLng(listOfRestaurants.get(position).getGeometry().getLocation().getLat(), listOfRestaurants.get(position).getGeometry().getLocation().getLng());
        LatLng endLatLng = new LatLng(MapViewFragment.myPosition.latitude, MapViewFragment.myPosition.longitude);
        int distance = (int) SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
        String theDistance = String.valueOf(distance);
        String m = "m";
        holder.restaurantDistance.setText(theDistance + m);

        int n = 0;
        if (listOfUserWhoChoseWhereLunch != null) {
            for (int i = 0; i < listOfUserWhoChoseWhereLunch.size(); i++) {
                if (listOfUserWhoChoseWhereLunch.get(i).getEatingPlaceId().equals(listOfRestaurants.get(holder.getAdapterPosition()).getPlaceId())) {
                    n = n + 1;
                }
            }
            if (n > 0) {
                String start = "(";
                String end = ")";
                holder.numberOfCoworker.setText(MessageFormat.format("{0}{1}{2}", start, n, end));
                holder.coworkerIcon.setVisibility(View.VISIBLE);
            } else {
                holder.numberOfCoworker.setText("");
                holder.coworkerIcon.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listOfRestaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfRestaurant;
        TextView restaurantAddress;
        RatingBar ratingBar;
        TextView openingHour;
        ImageView restaurantPic;
        TextView restaurantDistance;
        TextView numberOfCoworker;
        ImageView coworkerIcon;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameOfRestaurant = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            ratingBar = itemView.findViewById(R.id.restaurant_rating);
            openingHour = itemView.findViewById(R.id.restaurant_opening_hours);
            restaurantPic = itemView.findViewById(R.id.restaurant_pic);
            restaurantDistance = itemView.findViewById(R.id.restaurant_distance);
            numberOfCoworker = itemView.findViewById(R.id.number_of_coworker);
            coworkerIcon = itemView.findViewById(R.id.coworker_icon);
        }
    }


}
