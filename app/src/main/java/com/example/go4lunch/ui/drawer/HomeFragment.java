package com.example.go4lunch.ui.drawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.ui.userdashboard.ListViewFragment;
import com.example.go4lunch.ui.userdashboard.MapViewFragment;
import com.example.go4lunch.ui.userdashboard.WorkmatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setupListeners(root);
        displayMapFragment();
        return root;
    }

    private void setupListeners(View root) {

        BottomNavigationView bottomNavigationView = root.findViewById(R.id.bottom_navigation);
                bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        displayMapFragment();
                        return true;
                    case R.id.page_2:
                        getChildFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_container_view, ListViewFragment.class, null)
                                .commit();
                        return true;
                    case R.id.page_3:
                        getChildFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_container_view, WorkmatesFragment.class, null)
                                .commit();
                        return true;
                    default:
                        return false;
                }
            }

        });

    }

    private void displayMapFragment() {
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MapViewFragment.class, null)
                .commit();
    }
}
