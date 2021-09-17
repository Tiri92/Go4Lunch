package com.go4lunch.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.model.nearbysearch.NearbySearch;

public class ListViewFragment extends Fragment {

    public ListViewViewModel listViewViewModel;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.RecyclerView);

        listViewViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(ListViewViewModel.class);
        listViewViewModel.getNearbySearchResultFromVM().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override
            public void onChanged(NearbySearch nearbySearch) {
                mAdapter = new ListViewFragmentAdapter(nearbySearch.getResults());
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        return root;
    }
}
