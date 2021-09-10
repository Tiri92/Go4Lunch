package com.go4lunch.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.go4lunch.R;
import com.go4lunch.databinding.ActivityRestaurantDetailBinding;

public class RestaurantDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        ActivityRestaurantDetailBinding binding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());

    }
}
