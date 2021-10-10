package com.go4lunch.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.go4lunch.R;
import com.go4lunch.ui.home.RestaurantDetailActivity;

public class LunchFragment extends Fragment {

    LunchFragmentViewModel lunchFragmentFragmentViewModel;
    View view;
    String eatingPlaceId;
    String eatingPlace;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_fragment_lunch, container, false);
        lunchFragmentFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(LunchFragmentViewModel.class);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.your_lunch);

        lunchFragmentFragmentViewModel.getUserData().addOnSuccessListener(user -> {
            eatingPlaceId = user.getEatingPlaceId();
            eatingPlace = user.getEatingPlace();
            if (user.getEatingPlaceId().equals(" ")) {
                Toast.makeText(requireContext(), getString(R.string.not_selected_eating_place), Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();

            } else {
                lunchFragmentFragmentViewModel.callRestaurantDetail(eatingPlaceId);
                Intent intent = new Intent(requireContext(), RestaurantDetailActivity.class);
                intent.putExtra("placeId", eatingPlaceId);
                intent.putExtra("name", eatingPlace);
                ActivityCompat.startActivity(requireContext(), intent, null);

            }
        });

        AppCompatButton yourLunchButton = view.findViewById(R.id.your_lunch_button);
        yourLunchButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RestaurantDetailActivity.class);
            intent.putExtra("placeId", eatingPlaceId);
            intent.putExtra("name", eatingPlace);
            ActivityCompat.startActivity(requireContext(), intent, null);
        });

        return view;
    }


}
