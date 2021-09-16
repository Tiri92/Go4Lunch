package com.go4lunch.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.databinding.ActivityRestaurantDetailBinding;
import com.go4lunch.model.details.SearchDetail;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ActivityRestaurantDetailBinding binding;
    public RestaurantDetailViewModel restaurantDetailViewModel;
    String placeId;

    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRecyclerView = binding.restaurantDetailsRecyclerView;

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");

        restaurantDetailViewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
        restaurantDetailViewModel.callRestaurantDetail(placeId);
        restaurantDetailViewModel.getSearchDetailResultFromVM().observe(this, new Observer<SearchDetail>() {
            @Override
            public void onChanged(SearchDetail searchDetail) {

                TextView restaurantName = binding.restaurantDetailsName;
                restaurantName.setText(searchDetail.getResult().getName());

                TextView restaurantAddress = binding.restaurantDetailsAddress;
                restaurantAddress.setText(searchDetail.getResult().getVicinity());

                ImageView restaurantPic = binding.restaurantDetailPic;
                try {
                    String base = "https://maps.googleapis.com/maps/api/place/photo?";
                    String key = "key=" + BuildConfig.MAPS_API_KEY;
                    String reference = "&photoreference=" + searchDetail.getResult().getPhotos().get(0).getPhotoReference();
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
        });

    }

}
