package com.go4lunch.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.go4lunch.R;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.ResultsItem;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("RestrictedApi")
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationCallback locationCallback;
    public static LatLng myPosition;
    public MapViewViewModel mapViewViewModel;
    private final List<User> listOfUserWhoChose = new ArrayList<>();
    private final List<ResultsItem> listOfRestaurants = new ArrayList<>();

    private static final int LOCATION_REQUEST_INTERVAL_MS = 10_000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 25;

    private static final int REQUEST_CHECK_SETTINGS = 111;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mapViewViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MapViewViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map_view, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.i_m_hungry));

        checkGpsState();

        startLocationRequest();

        FloatingActionButton positionButton = root.findViewById(R.id.position_button);
        positionButtonListener(positionButton);

        return root;

    }

    private void setupObserver() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(myPosition)
                .title("My position"));
        mapViewViewModel.getAutocompleteSearchResultFromVM().observe(getViewLifecycleOwner(), autocompleteSearch -> {
            for (DetailSearch detailSearch : autocompleteSearch) {
                LatLng restaurantPositionVac = new LatLng(detailSearch.getResult().getGeometry().getLocation().getLat(),
                        detailSearch.getResult().getGeometry().getLocation().getLng());

                String placeId = detailSearch.getResult().getPlaceId();
                String name = detailSearch.getResult().getName();

                MarkerOptions restaurantMarkerOptionsVac;
                if (isBookedOrNot(placeId, listOfUserWhoChose)) {
                    restaurantMarkerOptionsVac = new MarkerOptions()
                            .position(restaurantPositionVac)
                            .icon(BitmapFromVector(requireContext(), R.drawable.baseline_booked_restaurant_24));
                } else {
                    restaurantMarkerOptionsVac = new MarkerOptions()
                            .position(restaurantPositionVac)
                            .icon(BitmapFromVector(requireContext(), R.drawable.baseline_unreserved_restaurant_24));
                }
                Marker restaurantMarkerVac = mMap.addMarker(restaurantMarkerOptionsVac);
                restaurantMarkerVac.setTag(placeId);
                restaurantMarkerVac.setTitle(name);
            }
        });
    }

    @VisibleForTesting
    public boolean isBookedOrNot(String placeId, List<User> users) {
        for (User myUser : users) {
            if (placeId.equals(myUser.getEatingPlaceId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_a_restaurant));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (myPosition != null) {
                    mapViewViewModel.callAutocompleteSearch(myPosition.latitude + "," + myPosition.longitude, query);
                    setupObserver();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(myPosition)
                        .title("My position"));
                displayMarkerOnRestaurantPosition(listOfRestaurants);
            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * Here we check if Gps is enabled for geo-location, and if he's we don't need to ask permission to activate geo-location, we just
     * display a toast in the method checkGpsState() for say to user that GPS is already enabled, if he's not we enter in the "else" of
     * checkGpsState() method and ask permission to activate geo-location
     */
    public static boolean isGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * In this method we ask the user for permission to activate geo-location to make startLocationRequest() work great
     */
    private void checkGpsState() {
        LocationManager locationManager = getSystemService(requireActivity(), LocationManager.class);

        if (isGpsEnabled(locationManager)) {
            Toast.makeText(getApplicationContext(), getString(R.string.gps_already_enabled), Toast.LENGTH_SHORT).show();
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            settingsBuilder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(settingsBuilder.build());

            task.addOnCompleteListener(task1 -> {
                try {
                    LocationSettingsResponse response = task1.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        requireActivity(),
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    /**
     * In this method we ask the user for location permission and when he accept, we launch a network request for get his last location
     */
    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    private void startLocationRequest() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0
        );

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mMap != null) {
                        moveAndDisplayMyPosition();
                        if (myPosition != null) {
                            mapViewViewModel.callNearbySearch(myPosition.latitude + "," + myPosition.longitude);
                        }
                    }
                }
            };
        }

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireActivity().getApplication());

        client.removeLocationUpdates(locationCallback);

        client.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                        .setInterval(LOCATION_REQUEST_INTERVAL_MS),
                locationCallback,
                Looper.getMainLooper()
        );

    }

    /**
     * Method called for display marker on restaurant position
     */

    int isBooked = 0;
    Marker restaurantMarker;
    MarkerOptions restaurantMarkerOptions;

    private void displayMarkerOnRestaurantPosition(List<ResultsItem> results) {
        if (getView() != null) {
            mapViewViewModel.getListOfUsersWhoChoseRestaurant().observe(getViewLifecycleOwner(), users -> {
                listOfUserWhoChose.clear();
                listOfUserWhoChose.addAll(users);
                for (ResultsItem myRestaurant : results) {
                    LatLng restaurantPosition = new LatLng(myRestaurant.getGeometry().getLocation().getLat(),
                            myRestaurant.getGeometry().getLocation().getLng());
                    isBooked = 0;
                    for (User myUser : users) {
                        if (myRestaurant.getPlaceId().equals(myUser.getEatingPlaceId())) {
                            isBooked++;
                        } else {
                            Log.e("Nothing", "Nothing");
                        }
                    }
                    if (isBooked != 0) {
                        restaurantMarkerOptions = new MarkerOptions()
                                .position(restaurantPosition)
                                .icon(BitmapFromVector(requireContext(), R.drawable.baseline_booked_restaurant_24));
                    } else {
                        restaurantMarkerOptions = new MarkerOptions()
                                .position(restaurantPosition)
                                .icon(BitmapFromVector(requireContext(), R.drawable.baseline_unreserved_restaurant_24));
                    }
                    restaurantMarker = mMap.addMarker(restaurantMarkerOptions);
                    restaurantMarker.setTag(myRestaurant.getPlaceId());
                    restaurantMarker.setTitle(myRestaurant.getName());
                }
            });
            mMap.setOnMarkerClickListener(marker -> {
                if (!marker.getTitle().equals("My position")) {
                    Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
                    intent.putExtra("placeId", marker.getTag().toString());
                    intent.putExtra("name", marker.getTitle());
                    ActivityCompat.startActivity(getContext(), intent, null);
                }
                return false;
            });
        }
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Method called for move the camera to the user position on map after recuperate his location
     */
    private void moveAndDisplayMyPosition() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(myPosition)
                .title("My position"));
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(myPosition).
                zoom(14).
                tilt(30).
                bearing(0).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Callback called when GoogleMap is ready/created
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (myPosition == null) {
            LatLng startPosition = new LatLng(-34, 151); //new LatLng(48, 12);
            mMap.addMarker(new MarkerOptions()
                    .position(startPosition)
                    .title("Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(startPosition));
        } else {
            moveAndDisplayMyPosition();
        }
        mapViewViewModel.getNearbySearchResultFromVM().observe(getViewLifecycleOwner(), nearbySearch -> {
            listOfRestaurants.clear();
            listOfRestaurants.addAll(nearbySearch.getResults());
            displayMarkerOnRestaurantPosition(nearbySearch.getResults());
        });
    }

    private void positionButtonListener(FloatingActionButton positionButton) {
        positionButton.setOnClickListener(v -> {
            LocationManager locationManager = getSystemService(requireActivity(), LocationManager.class);
            if (isGpsEnabled(locationManager) & myPosition != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
            } else {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                settingsBuilder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

                Task<LocationSettingsResponse> task =
                        LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(settingsBuilder.build());

                task.addOnCompleteListener(task1 -> {
                    try {
                        LocationSettingsResponse response = task1.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            requireActivity(),
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }
        });
    }


}

