package com.go4lunch.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.go4lunch.R;

public class LogoutFragment extends Fragment {

    public LogoutFragmentViewModel logoutFragmentViewModel;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_fragment_logout, container, false);
        logoutFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(LogoutFragmentViewModel.class);

        // Logout popup
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.want_logout)
                .setPositiveButton(R.string.yes, (dialogInterface, i) ->
                        logoutFragmentViewModel.logout(requireContext())
                                .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(requireContext(), getString(R.string.successful_disconnection), Toast.LENGTH_SHORT).show();
                                        }
                                )
                )
                .setNegativeButton(R.string.cancel, null)
                .show();

        setUpListener();

        return view;
    }

    private void setUpListener() {

        // Logout button
        AppCompatButton logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {

            new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.want_logout)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) ->
                            logoutFragmentViewModel.logout(requireContext())
                                    .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(requireContext(), getString(R.string.successful_disconnection), Toast.LENGTH_SHORT).show();
                                            }
                                    )
                    )
                    .setNegativeButton(R.string.cancel, null)
                    .show();

        });
    }

}
