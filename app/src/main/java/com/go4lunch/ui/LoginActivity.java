package com.go4lunch.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public LoginActivityViewModel loginActivityViewModel;
    private static final int RC_SIGN_IN = 123;
    private ConstraintLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.Theme_AppCompat_NoActionBar);
        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);
        view = new ConstraintLayout(this);
        view.setBackgroundColor(Color.WHITE);
        setContentView(view);
        if (loginActivityViewModel.isCurrentUserLogged()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            startSignInActivity();
        }
    }

    private void startSignInActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.google_button)
                .setFacebookButtonId(R.id.facebook_button)
                .setEmailButtonId(R.id.email_button)
                .build();

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAuthMethodPickerLayout(customLayout)
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(false, true)
                        .build(), RC_SIGN_IN
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                loginActivityViewModel.createUser();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                if (response == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(getBaseContext(), getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        finish();
    }


}
