package com.go4lunch.ui.home.restaurantdetailactivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.databinding.ActivityRestaurantDetailBinding;
import com.go4lunch.model.firestore.User;
import com.go4lunch.ui.home.workmanager.EatingPlaceNotificationWorker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ActivityRestaurantDetailBinding binding;
    public RestaurantDetailViewModel restaurantDetailViewModel;
    List<String> listOfRestaurantsLiked;
    String placeId;
    String nameOfCurrentRestaurant;
    String addressOfCurrentRestaurant;
    String currentUsername;

    private RecyclerView mRecyclerView;
    RestaurantDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRecyclerView = binding.restaurantDetailsRecyclerView;

        restaurantDetailViewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
        restaurantDetailViewModel.getUserData().addOnSuccessListener(user -> {
            listOfRestaurantsLiked = user.getListOfRestaurantsLiked();
            setLikeButtonChecked(listOfRestaurantsLiked.contains(nameOfCurrentRestaurant));
        });

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        nameOfCurrentRestaurant = intent.getStringExtra("name");
        addressOfCurrentRestaurant = intent.getStringExtra("address");

        restaurantDetailViewModel.getListOfUsersWhoChoseRestaurant().observe(this, users -> restaurantDetailViewModel.getUserData().addOnSuccessListener(myUser -> {
            List<User> sortedUserList = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getEatingPlaceId().equals(placeId) & !users.get(i).getUid().equals(myUser.getUid())) {
                    sortedUserList.add(users.get(i));
                }
            }
            mAdapter = new RestaurantDetailAdapter(sortedUserList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            mRecyclerView.setAdapter(mAdapter);
        }));

        restaurantDetailViewModel.callRestaurantDetail(placeId);
        restaurantDetailViewModel.getSearchDetailResultFromVM().observe(this, detailSearch -> {

            TextView restaurantName = binding.restaurantDetailsName;
            restaurantName.setText(detailSearch.getResult().getName());

            TextView restaurantAddress = binding.restaurantDetailsAddress;
            restaurantAddress.setText(detailSearch.getResult().getVicinity());

            RatingBar ratingBar = binding.restaurantDetailsRating;
            float getRatingOnThree = (float) (detailSearch.getResult().getRating() / 1.66);
            ratingBar.setRating(getRatingOnThree);

            ImageView restaurantPic = binding.restaurantDetailPic;
            try {
                if (detailSearch.getResult().getPhotos() != null) {
                    String base = "https://maps.googleapis.com/maps/api/place/photo?";
                    String key = "key=" + BuildConfig.MAPS_API_KEY;
                    String reference = "&photoreference=" + detailSearch.getResult().getPhotos().get(0).getPhotoReference();
                    String maxH = "&maxheight=157";
                    String maxW = "&maxwidth=157";
                    String query = base + key + reference + maxH + maxW;

                    Glide.with(restaurantPic)
                            .load(query)
                            .centerCrop()
                            .into(restaurantPic);
                } else {
                    restaurantPic.setImageResource(R.drawable.ic_baseline_no_photography_24);
                }
            } catch (Exception e) {
                Log.i("[THIERRY]", "Exception : " + e.getMessage());
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
                        this.notificationWorker();
                    }

                    public long getMillisecondsUntilAHours(int hours, int minutes) {
                        Calendar dueDate = Calendar.getInstance();
                        Calendar currentDate = Calendar.getInstance();
                        dueDate.set(Calendar.HOUR_OF_DAY, hours);
                        dueDate.set(Calendar.MINUTE, minutes);
                        dueDate.set(Calendar.SECOND, 0);
                        if (dueDate.before(currentDate)) {
                            dueDate.add(Calendar.HOUR_OF_DAY, 24);
                        }
                        return dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
                    }

                    private void notificationWorker() {
                        restaurantDetailViewModel.getUserData().addOnSuccessListener(user -> {
                            currentUsername = user.getUsername();
                            Data data = new Data.Builder()
                                    .putString(EatingPlaceNotificationWorker.KEY_EATING_PLACE, nameOfCurrentRestaurant)
                                    .putString(EatingPlaceNotificationWorker.USER_NAME, currentUsername)
                                    .putString(EatingPlaceNotificationWorker.KEY_EATING_PLACE_ID, placeId)
                                    .putString(EatingPlaceNotificationWorker.KEY_EATING_PLACE_ADDRESS, addressOfCurrentRestaurant)
                                    .putString(EatingPlaceNotificationWorker.KEY_NOTIFICATION_MESSAGE_JOIN, getString(R.string.notification_joining))
                                    .putString(EatingPlaceNotificationWorker.KEY_NOTIFICATION_TITLE, getString(R.string.notification_title))
                                    .putString(EatingPlaceNotificationWorker.KEY_NOTIFICATION_MESSAGE, getString(R.string.notification_message))
                                    .build();
                            OneTimeWorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(EatingPlaceNotificationWorker.class)
                                    .setInputData(data)
                                    .setInitialDelay(getMillisecondsUntilAHours(12, 0), TimeUnit.MILLISECONDS)
                                    .build();
                            WorkManager.getInstance(getBaseContext()).enqueueUniqueWork(getString(R.string.notification), ExistingWorkPolicy.REPLACE, dailyWorkRequest);
                        });
                    }

                }).addOnFailureListener(e -> showSnackBar(getString(R.string.error_chosen_restaurant)));
            }
        });

        Button callButton = binding.callButton;
        callButton.setOnClickListener(v -> {
            String tel = "tel:";
            if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber() != null) {
                Intent startPhoneCall = new Intent(Intent.ACTION_DIAL);
                startPhoneCall.setData(Uri.parse(tel + restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber()));
                startActivity(startPhoneCall);
            } else {
                showSnackBar(getString(R.string.no_phone_number_available));
            }
        });

        MaterialButton likeButton = binding.likeButton;
        likeButton.setOnClickListener(v -> {
            if (listOfRestaurantsLiked.contains(nameOfCurrentRestaurant)) {
                listOfRestaurantsLiked.remove(nameOfCurrentRestaurant);
                restaurantDetailViewModel.updateListOfRestaurantsLiked(listOfRestaurantsLiked).addOnSuccessListener(unused -> {
                    showSnackBar(getString(R.string.restaurant_unliked));
                    setLikeButtonChecked(false);
                }).addOnFailureListener(e -> showSnackBar(getString(R.string.error_unlike)));
            } else if (!listOfRestaurantsLiked.contains(nameOfCurrentRestaurant)) {
                listOfRestaurantsLiked.add(nameOfCurrentRestaurant);
                restaurantDetailViewModel.updateListOfRestaurantsLiked(listOfRestaurantsLiked).addOnSuccessListener(unused -> {
                    showSnackBar(getString(R.string.restaurant_liked));
                    setLikeButtonChecked(true);
                }).addOnFailureListener(e -> showSnackBar(getString(R.string.error_like)));
            }
        });

        Button websiteButton = binding.websiteButton;
        websiteButton.setOnClickListener(v -> {
            if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite() != null) {
                Intent openWebsite = new Intent(Intent.ACTION_VIEW);
                openWebsite.setData(Uri.parse(restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite()));
                startActivity(openWebsite);
            } else {
                showSnackBar(getString(R.string.no_website_available));
            }
        });

    }

    private void setLikeButtonChecked(Boolean checked) {
        Drawable likeButtonDrawable = binding.likeButton.getCompoundDrawables()[1];
        if (checked) {
            likeButtonDrawable.setColorFilter(ContextCompat.getColor(this, R.color.Yellow), PorterDuff.Mode.SRC_IN);
        } else {
            likeButtonDrawable.setColorFilter(ContextCompat.getColor(this, R.color.orange), PorterDuff.Mode.SRC_IN);
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }


}

