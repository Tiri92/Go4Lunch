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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.model.User;
import com.go4lunch.ui.main.SettingsFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorkmatesFragment extends Fragment {

    View view;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    public WorkmatesFragmentViewModel workmatesFragmentViewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workmates, container, false);
        workmatesFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(WorkmatesFragmentViewModel.class);
        mRecyclerView = view.findViewById(R.id.workmates_fragment_recycler_view);

        workmatesFragmentViewModel.getListOfUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter = new WorkmatesFragmentAdapter(users);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        return view;
    }
}
