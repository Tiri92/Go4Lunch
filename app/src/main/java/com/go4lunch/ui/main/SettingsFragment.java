package com.go4lunch.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.go4lunch.R;
import com.go4lunch.ui.LoginActivity;
import com.go4lunch.ui.home.RestaurantDetailActivity;

import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    View view;
    @Nullable
    public AlertDialog dialog = null;
    public SettingsFragmentViewModel settingsFragmentViewModel;

    private static final Pattern upperCaseLetter = //TODO Correct upperCaseLetter and maximumLetter Regex
            Pattern.compile("^[A-Z]{1,}+[a-z]{1,}");

    private static final Pattern maximumLetter =
            Pattern.compile("[a-zA-Z]{0,13}");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_fragment_settings, container, false);
        settingsFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(SettingsFragmentViewModel.class);
        setupListeners();
        return view;
    }


    private void updateName(String uid, String username) {
        if (!username.isEmpty()) {
            settingsFragmentViewModel.updateUsername(username);
            Toast.makeText(requireContext(), getString(R.string.username_updated), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), getString(R.string.username_not_updated), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {

        // Delete button
        AppCompatButton deleteAccountButton = view.findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(view -> {

            new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.want_delete)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) ->
                            settingsFragmentViewModel.deleteUser(requireContext())
                                    .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(requireContext(), getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
                                            }
                                    )
                    )
                    .setNegativeButton(R.string.cancel, null)
                    .show();

        });

        /**
         * Update username button, DialogChangeUserName
         **/
        AppCompatButton updateUsernameButton = view.findViewById(R.id.update_button);
        updateUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeUserNameDialog();
            }
        });

    } // setupListeners finish here

    private void showChangeUserNameDialog() {
        final AlertDialog dialog = getChangeUserNameDialog();

        dialog.show();

    }

    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        assert dialog != null;
        EditText dialogEditText = dialog.findViewById(R.id.edittext_username);
        // If dialog is open
        if (dialogEditText != null) {
            // Get the new username
            String newUserName = dialogEditText.getText().toString();

            // If the text field is empty
            if (newUserName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.cannot_be_empty));
            }
            // If it doesn't start with one upper case characters
            else if (!upperCaseLetter.matcher(newUserName).matches()) {
                dialogEditText.setError(getString(R.string.upper_case_letter));
            }
            // If the name is too long
            else if (!maximumLetter.matcher(newUserName).matches()) {
                dialogEditText.setError(getString(R.string.maximum_letters));
            }
            // If all is okay
            else {
                updateName(settingsFragmentViewModel.getCurrentUser().getUid(), dialogEditText.getText().toString());
                dialogInterface.dismiss();
            }

        } else {
            dialogInterface.dismiss();
        }
    }


    @NonNull
    private AlertDialog getChangeUserNameDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());

        alertBuilder.setTitle(R.string.change_username);
        alertBuilder.setView(R.layout.dialog_change_username);
        alertBuilder.setPositiveButton(R.string.save, null);
        alertBuilder.setNegativeButton(R.string.cancel, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialog = null;
            }
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onPositiveButtonClick(dialog);
                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    }

}

