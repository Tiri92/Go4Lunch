package com.go4lunch.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.di.DI;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ListViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);

        mAdapter = new ListViewFragmentAdapter(Objects.requireNonNull(DI.getGooglePlaceRepository().getNearbySearchResult().getValue()).getResults());
        mRecyclerView = root.findViewById(R.id.RecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }
}
