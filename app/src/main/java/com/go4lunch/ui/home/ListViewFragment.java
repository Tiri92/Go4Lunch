package com.go4lunch.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.di.DI;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.nearbysearch.NearbySearch;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ListViewFragment extends Fragment {

    public MapViewViewModel mapViewViewModel;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.RecyclerView);

        mapViewViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MapViewViewModel.class);
        mapViewViewModel.getNearbySearchResultFromVM().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override
            public void onChanged(NearbySearch nearbySearch) {
                mAdapter = new ListViewFragmentAdapter(nearbySearch.getResults());
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        return root;
    }
}
