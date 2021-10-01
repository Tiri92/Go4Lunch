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
import com.go4lunch.model.firestore.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    View view;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter<WorkmatesFragmentAdapter.ViewHolder> mAdapter;
    public WorkmatesFragmentViewModel workmatesFragmentViewModel;
    List<User> mUsers = new ArrayList<>();

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workmates, container, false);
        workmatesFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(WorkmatesFragmentViewModel.class);
        mRecyclerView = view.findViewById(R.id.workmates_fragment_recycler_view);

        mAdapter = new WorkmatesFragmentAdapter(mUsers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mAdapter);

        workmatesFragmentViewModel.getListOfUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mUsers.clear();
                mUsers.addAll(users);
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
