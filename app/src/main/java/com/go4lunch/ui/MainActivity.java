package com.go4lunch.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.go4lunch.R;
import com.go4lunch.databinding.ActivityMainBinding;
import com.go4lunch.ui.home.RestaurantDetailViewModel;
import com.go4lunch.ui.main.SettingsFragmentViewModel;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    public SettingsFragmentViewModel settingsFragmentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(binding.drawerLayout)
                        .build();

        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);


        settingsFragmentViewModel = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
        if (settingsFragmentViewModel.isCurrentUserLogged()) {
            settingsFragmentViewModel.getUserData().addOnSuccessListener(user -> {
                // Set the data with the user information

                String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.no_username_found) : user.getUsername();
                TextView usernameTextView = binding.navView.getHeaderView(0).findViewById(R.id.username_field);
                usernameTextView.setText(username);

                FirebaseUser firebaseUser = settingsFragmentViewModel.getCurrentUser();
                String userEmail = TextUtils.isEmpty(firebaseUser.getEmail()) ? getString(R.string.no_username_found) : firebaseUser.getEmail();
                TextView userEmailTextView = binding.navView.getHeaderView(0).findViewById(R.id.user_email_field);
                userEmailTextView.setText(userEmail);
            });
        }

    }

}




