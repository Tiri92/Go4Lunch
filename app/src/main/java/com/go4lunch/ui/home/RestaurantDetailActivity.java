package com.go4lunch.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.databinding.ActivityRestaurantDetailBinding;
import com.go4lunch.model.details.SearchDetail;
import com.go4lunch.model.firestore.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ActivityRestaurantDetailBinding binding;
    public RestaurantDetailViewModel restaurantDetailViewModel;
    List<String> listOfRestaurantsLiked;
    String placeId;
    String nameOfCurrentRestaurant;

    private RecyclerView mRecyclerView;
    RecyclerView.Adapter<RestaurantDetailAdapter.ViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRecyclerView = binding.restaurantDetailsRecyclerView;

        restaurantDetailViewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
        restaurantDetailViewModel.getUserData().addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                listOfRestaurantsLiked = user.getListOfRestaurantsLiked();
            }
        });

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        nameOfCurrentRestaurant = intent.getStringExtra("name");

        restaurantDetailViewModel.getListOfUsersWhoChoseRestaurant().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                List<User> sortedUserList = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEatingPlaceId().equals(placeId)) {
                        sortedUserList.add(users.get(i));
                    }
                }
                mAdapter = new RestaurantDetailAdapter(sortedUserList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                mRecyclerView.setAdapter(mAdapter);
            }
        });

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

        FloatingActionButton chosenRestaurantButton = binding.chosenRestaurantFloatingBtn;
        chosenRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantDetailViewModel.getUserData().addOnSuccessListener(new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (placeId.equals(user.getEatingPlaceId())) {
                            restaurantDetailViewModel.updateEatingPlaceId(" ");
                            restaurantDetailViewModel.updateEatingPlace(" ");
                            showSnackBar(getString(R.string.choice_canceled));
                        } else if (user.getEatingPlaceId().equals(" ")) {
                            restaurantDetailViewModel.updateEatingPlaceId(user.setEatingPlaceId(placeId));
                            restaurantDetailViewModel.updateEatingPlace(user.setEatingPlace(nameOfCurrentRestaurant));
                            showSnackBar(getString(R.string.success_chosen_restaurant));
                        } else if (!user.getEatingPlaceId().equals(placeId)) {
                            restaurantDetailViewModel.updateEatingPlaceId(user.setEatingPlaceId(placeId));
                            restaurantDetailViewModel.updateEatingPlace(user.setEatingPlace(nameOfCurrentRestaurant));
                            showSnackBar(getString(R.string.choice_updated));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        showSnackBar(getString(R.string.error_chosen_restaurant));
                    }
                });
            }
        });

        BottomNavigationView bottomNavigationView = binding.restaurantDetailsNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.call_details:
                        String tel = "tel:";
                        if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber() != null) {
                            Intent startPhoneCall = new Intent(Intent.ACTION_DIAL);
                            startPhoneCall.setData(Uri.parse(tel + restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber()));
                            startActivity(startPhoneCall);
                        } else {
                            showSnackBar(getString(R.string.no_phone_number_available));
                        }
                        return true;
                    case R.id.like_details:
                        if (listOfRestaurantsLiked.contains(nameOfCurrentRestaurant)) {
                            listOfRestaurantsLiked.remove(nameOfCurrentRestaurant);
                            restaurantDetailViewModel.updateListOfRestaurantsLiked(listOfRestaurantsLiked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showSnackBar(getString(R.string.restaurant_unliked));
                                    MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.like_details);
                                    menuItem.setIcon(R.drawable.detail_menu_star_24); //TODO Not working
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    showSnackBar(getString(R.string.error_unlike));
                                }
                            });

                        } else if (!listOfRestaurantsLiked.contains(nameOfCurrentRestaurant)) {
                            listOfRestaurantsLiked.add(nameOfCurrentRestaurant);
                            restaurantDetailViewModel.updateListOfRestaurantsLiked(listOfRestaurantsLiked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showSnackBar(getString(R.string.restaurant_liked));
                                    MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.like_details);
                                    menuItem.setIcon(R.drawable.detail_menu_yellow_star_24); //TODO Not working
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    showSnackBar(getString(R.string.error_like));
                                }
                            });

                        }
                        return true;
                    case R.id.website_details:
                        if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite() != null) {
                            Intent openWebsite = new Intent(Intent.ACTION_VIEW);
                            openWebsite.setData(Uri.parse(restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite()));
                            startActivity(openWebsite);
                        } else {
                            showSnackBar(getString(R.string.no_website_available));
                        }
                        return true;
                }
                return false;
            }
        });

    }

    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}

