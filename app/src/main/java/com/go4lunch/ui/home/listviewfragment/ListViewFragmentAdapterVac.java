package com.go4lunch.ui.home.listviewfragment;

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
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.ui.home.mapviewfragment.MapViewFragment;
import com.go4lunch.ui.home.restaurantdetailactivity.RestaurantDetailActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.MessageFormat;
import java.util.List;

public class ListViewFragmentAdapterVac extends RecyclerView.Adapter<ListViewFragmentAdapterVac.ViewHolder> {

    View itemView;
    private final List<DetailSearch> listOfRestaurantVac;
    private final List<User> listOfUserWhoChose;

    public ListViewFragmentAdapterVac(List<DetailSearch> listOfRestaurantVac, List<User> listOfUserWhoChose) {
        this.listOfRestaurantVac = listOfRestaurantVac;
        this.listOfUserWhoChose = listOfUserWhoChose;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("placeId", listOfRestaurantVac.get(holder.getBindingAdapterPosition()).getResult().getPlaceId());
            intent.putExtra("name", listOfRestaurantVac.get(holder.getBindingAdapterPosition()).getResult().getName());
            ActivityCompat.startActivity(v.getContext(), intent, null);
        });

        holder.nameOfRestaurant.setText(listOfRestaurantVac.get(position).getResult().getName());

        holder.restaurantAddress.setText(listOfRestaurantVac.get(position).getResult().getVicinity());

        float getRatingOnThree = (float) (listOfRestaurantVac.get(position).getResult().getRating() / 1.66);
        holder.ratingBar.setRating(getRatingOnThree);

        if (listOfRestaurantVac.get(position).getResult().getOpeningHours() != null) {
            if (listOfRestaurantVac.get(position).getResult().getOpeningHours().isOpenNow()) {
                holder.openingHour.setText(R.string.Open_now);
            } else {
                holder.openingHour.setText(R.string.Close_now);
            }
        } else {
            holder.openingHour.setText(R.string.we_dont_know);
        }

        try {
            if (listOfRestaurantVac.get(position).getResult().getPhotos() != null) {
                String base = "https://maps.googleapis.com/maps/api/place/photo?";
                String key = "key=" + BuildConfig.MAPS_API_KEY;
                String reference = "&photoreference=" + listOfRestaurantVac.get(position).getResult().getPhotos().get(0).getPhotoReference();
                String maxH = "&maxheight=157";
                String maxW = "&maxwidth=157";
                String query = base + key + reference + maxH + maxW;

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

        LatLng startLatLng = new LatLng(listOfRestaurantVac.get(position).getResult().getGeometry().getLocation().getLat(), listOfRestaurantVac.get(position).getResult().getGeometry().getLocation().getLng());
        LatLng endLatLng = new LatLng(MapViewFragment.myPosition.latitude, MapViewFragment.myPosition.longitude);
        int distance = (int) SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
        String theDistance = String.valueOf(distance);
        String m = "m";
        holder.restaurantDistance.setText(MessageFormat.format("{0}{1}", theDistance, m));

        int n = 0;
        if (listOfUserWhoChose != null) {
            for (int i = 0; i < listOfUserWhoChose.size(); i++) {
                if (listOfUserWhoChose.get(i).getEatingPlaceId().equals(listOfRestaurantVac.get(holder.getBindingAdapterPosition()).getResult().getPlaceId())) {
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
        return listOfRestaurantVac.size();
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

        public ViewHolder(@NonNull View itemView) {
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
