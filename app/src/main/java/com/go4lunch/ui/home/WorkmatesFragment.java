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

import org.jetbrains.annotations.NotNull;

public class WorkmatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);

        mRecyclerView = root.findViewById(R.id.workmates_fragment_recycler_view);

        return root;
    }
}
