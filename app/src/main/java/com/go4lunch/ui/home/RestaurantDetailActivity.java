package com.go4lunch.ui.home;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
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
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.ui.home.workmanager.EatingPlaceNotificationWorker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ActivityRestaurantDetailBinding binding;
    public RestaurantDetailViewModel restaurantDetailViewModel;
    List<String> listOfRestaurantsLiked;
    String placeId;
    String nameOfCurrentRestaurant;
    String addressOfCurrentRestaurant;

    private RecyclerView mRecyclerView;
    RestaurantDetailAdapter mAdapter;

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
                setLikeButtonChecked(listOfRestaurantsLiked.contains(nameOfCurrentRestaurant));
            }
        });

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        nameOfCurrentRestaurant = intent.getStringExtra("name");
        addressOfCurrentRestaurant = intent.getStringExtra("address");

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
        restaurantDetailViewModel.getSearchDetailResultFromVM().observe(this, new Observer<DetailSearch>() {
            @Override
            public void onChanged(DetailSearch detailSearch) {

                TextView restaurantName = binding.restaurantDetailsName;
                restaurantName.setText(detailSearch.getResult().getName());

                TextView restaurantAddress = binding.restaurantDetailsAddress;
                restaurantAddress.setText(detailSearch.getResult().getVicinity());

                RatingBar ratingBar = binding.restaurantDetailsRating;
                float getRatingOnThree = (float) (detailSearch.getResult().getRating() / 1.66);
                ratingBar.setRating(getRatingOnThree);

                ImageView restaurantPic = binding.restaurantDetailPic;
                try {
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
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(User user) {
                        if (placeId.equals(user.getEatingPlaceId())) {
                            restaurantDetailViewModel.updateEatingPlaceId(" ");
                            restaurantDetailViewModel.updateEatingPlace(" ");
                            WorkManager.getInstance(getBaseContext()).cancelUniqueWork(getString(R.string.notification));
                            showSnackBar(getString(R.string.choice_canceled));
                        } else if (user.getEatingPlaceId().equals(" ")) {
                            restaurantDetailViewModel.updateEatingPlaceId(user.setEatingPlaceId(placeId));
                            restaurantDetailViewModel.updateEatingPlace(user.setEatingPlace(nameOfCurrentRestaurant));
                            this.notificationWorker();
                            showSnackBar(getString(R.string.success_chosen_restaurant));
                        } else if (!user.getEatingPlaceId().equals(placeId)) {
                            restaurantDetailViewModel.updateEatingPlaceId(user.setEatingPlaceId(placeId));
                            restaurantDetailViewModel.updateEatingPlace(user.setEatingPlace(nameOfCurrentRestaurant));
                            WorkManager.getInstance(getBaseContext()).cancelUniqueWork(getString(R.string.notification));
                            this.notificationWorker();
                            showSnackBar(getString(R.string.choice_updated));
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public long getMillisecondsUntilAHours(int hours, int minutes){
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

                    String fakeNameOfUsers;

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    private void notificationWorker() { //TODO When i call this in an if, it doesn't display notification if application is close
                        Data data = new Data.Builder()
                                .putString(EatingPlaceNotificationWorker.KEY_EATING_PLACE, nameOfCurrentRestaurant)
                                .putString(EatingPlaceNotificationWorker.USER_NAME, fakeNameOfUsers)
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
                    }

                }).addOnFailureListener(e -> showSnackBar(getString(R.string.error_chosen_restaurant)));
            }
        });

        Button callButton = binding.callButton;
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:";
                if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber() != null) {
                    Intent startPhoneCall = new Intent(Intent.ACTION_DIAL);
                    startPhoneCall.setData(Uri.parse(tel + restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getFormattedPhoneNumber()));
                    startActivity(startPhoneCall);
                } else {
                    showSnackBar(getString(R.string.no_phone_number_available));
                }
            }
        });

        MaterialButton likeButton = binding.likeButton;
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfRestaurantsLiked.contains(nameOfCurrentRestaurant)) {
                    listOfRestaurantsLiked.remove(nameOfCurrentRestaurant);
                    restaurantDetailViewModel.updateListOfRestaurantsLiked(listOfRestaurantsLiked).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            showSnackBar(getString(R.string.restaurant_unliked));
                            setLikeButtonChecked(false);
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
                            setLikeButtonChecked(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            showSnackBar(getString(R.string.error_like));
                        }
                    });

                }
            }
        });

        Button websiteButton = binding.websiteButton;
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite() != null) {
                    Intent openWebsite = new Intent(Intent.ACTION_VIEW);
                    openWebsite.setData(Uri.parse(restaurantDetailViewModel.getSearchDetailResultFromVM().getValue().getResult().getWebsite()));
                    startActivity(openWebsite);
                } else {
                    showSnackBar(getString(R.string.no_website_available));
                }
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

