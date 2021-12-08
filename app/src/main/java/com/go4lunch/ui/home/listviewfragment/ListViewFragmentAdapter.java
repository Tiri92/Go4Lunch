package com.go4lunch.ui.home.listviewfragment;

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
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.ResultsItem;
import com.go4lunch.ui.home.mapviewfragment.MapViewFragment;
import com.go4lunch.ui.home.restaurantdetailactivity.RestaurantDetailActivity;
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
            intent.putExtra("placeId", listOfRestaurants.get(holder.getBindingAdapterPosition()).getPlaceId());
            intent.putExtra("name", listOfRestaurants.get(holder.getBindingAdapterPosition()).getName());
            intent.putExtra("address", listOfRestaurants.get(holder.getBindingAdapterPosition()).getVicinity());
            ActivityCompat.startActivity(v.getContext(), intent, null);
        });

        holder.nameOfRestaurant.setText(listOfRestaurants.get(position).getName());

        holder.restaurantAddress.setText(listOfRestaurants.get(position).getVicinity());

        holder.ratingBar.setRating(getRatingOnThree(listOfRestaurants.get(position).getRating()));

        holder.openingHour.setText(getOpeningHoursText(listOfRestaurants.get(position)));

        try {
            if (listOfRestaurants.get(position).getPhotos() != null) {
                String query = getPhotoRequest(listOfRestaurants.get(position).getPhotos().get(0).getPhotoReference());
                Glide.with(holder.restaurantPic)
                        .load(query)
                        .centerCrop()
                        .into(holder.restaurantPic);
            } else {
                holder.restaurantPic.setImageResource(R.drawable.ic_baseline_no_photography_24);
            }
        } catch (Exception e) {
            Log.i("[THIERRY]", "Exception : " + e.getMessage());
        }

        LatLng startLatLng = new LatLng(listOfRestaurants.get(position).getGeometry().getLocation().getLat(), listOfRestaurants.get(position).getGeometry().getLocation().getLng());
        LatLng endLatLng = new LatLng(MapViewFragment.myPosition.latitude, MapViewFragment.myPosition.longitude);
        int distance = (int) SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
        String theDistance = String.valueOf(distance);
        String m = "m";
        holder.restaurantDistance.setText(theDistance + m);

        if (listOfUserWhoChoseWhereLunch != null) {
            int n = getNumberOfReservations(listOfRestaurants.get(holder.getBindingAdapterPosition()).getPlaceId(), listOfUserWhoChoseWhereLunch);
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

    @VisibleForTesting
    public float getRatingOnThree(double rating) {
        return (float) (rating / 1.66);
    }

    @VisibleForTesting
    public int getOpeningHoursText(ResultsItem restaurant) {
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().isOpenNow()) {
                return R.string.Open_now;
            } else {
                return R.string.Close_now;
            }
        } else {
            return R.string.we_dont_know;
        }
    }

    @VisibleForTesting
    public String getPhotoRequest(String photoReference) {
        String base = "https://maps.googleapis.com/maps/api/place/photo?";
        String key = "key=" + BuildConfig.MAPS_API_KEY;
        String reference = "&photoreference=" + photoReference;
        String maxH = "&maxheight=157";
        String maxW = "&maxwidth=157";
        return base + key + reference + maxH + maxW;
    }

    @VisibleForTesting
    public int getNumberOfReservations(String placeId, List<User> listOfUserWhoChose) {
        int n = 0;
        for (int i = 0; i < listOfUserWhoChose.size(); i++) {
            if (listOfUserWhoChose.get(i).getEatingPlaceId().equals(placeId)) {
                n = n + 1;
            }
        }
        return n;
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
