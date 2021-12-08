package com.go4lunch.ui.home.workmatesfragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.model.firestore.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    View view;
    WorkmatesFragmentAdapter mAdapter;
    public WorkmatesFragmentViewModel workmatesFragmentViewModel;
    List<User> mUsers = new ArrayList<>();
    List<User> mUsersFull = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workmates, container, false);
        workmatesFragmentViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(WorkmatesFragmentViewModel.class);
        RecyclerView mRecyclerView = view.findViewById(R.id.workmates_fragment_recycler_view);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.available_workmates);

        mAdapter = new WorkmatesFragmentAdapter(mUsers, mUsersFull);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mAdapter);

        workmatesFragmentViewModel.getListOfUsers().observe(getViewLifecycleOwner(), users -> workmatesFragmentViewModel.getUserData().addOnSuccessListener(myUser -> {
            for (User user : users) {
                if (user.getUid().equals(myUser.getUid())) {
                    users.remove(user);
                    break;
                }
            }
            mUsers.clear();
            mUsers.addAll(users);
            mUsersFull.clear();
            mUsersFull.addAll(users);
            mAdapter.notifyDataSetChanged();
        }));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(getString(R.string.search_a_workmate));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {


            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }


}
