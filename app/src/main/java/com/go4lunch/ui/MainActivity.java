package com.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.go4lunch.R;
import com.go4lunch.databinding.ActivityMainBinding;
import com.go4lunch.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    public MainActivityViewModel mainActivityViewModel;
    public FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(binding.drawerLayout)
                        .build();

        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        /**
         * Set HeaderView text fields
         **/
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (mainActivityViewModel.isCurrentUserLogged()) {
            mainActivityViewModel.getUserData().addOnSuccessListener(user -> {
                // Set the data with the user information

                FirebaseUser firebaseUser = mainActivityViewModel.getCurrentUser();
                String userEmail = TextUtils.isEmpty(firebaseUser.getEmail()) ? getString(R.string.no_email_found) : firebaseUser.getEmail(); // Condition ternaire
                TextView userEmailTextView = binding.navView.getHeaderView(0).findViewById(R.id.user_email_field);
                userEmailTextView.setText(userEmail);

                ImageView userPic = binding.navView.getHeaderView(0).findViewById(R.id.nav_header_imageview); //TODO
            });
            mainActivityViewModel.getUserDataForUpdate().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                    //Verify if value return information about the current user
                    if (value == null)
                        return;
                    User user = value.toObject(User.class);
                    if (user != null) {
                        String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.no_username_found) : user.getUsername(); // Condition ternaire
                        TextView usernameTextView = binding.navView.getHeaderView(0).findViewById(R.id.username_field);
                        usernameTextView.setText(username);
                    }
                }
            });
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                if (!mainActivityViewModel.isCurrentUserLogged()) {
                    comeBackToLoginActivity();
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void comeBackToLoginActivity() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        Intent intent = new Intent(this, LoginActivity.class);
        ActivityCompat.startActivity(this, intent, null);
        finish();
    }
}




