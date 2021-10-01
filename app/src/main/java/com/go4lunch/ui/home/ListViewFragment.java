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
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.NearbySearch;
import com.go4lunch.model.nearbysearch.ResultsItem;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    public ListViewViewModel listViewViewModel;
    private RecyclerView mRecyclerView;
    RecyclerView.Adapter<ListViewFragmentAdapter.ViewHolder> mAdapter;
    public List<User> listOfUserWhoChose = new ArrayList<>();
    private List<ResultsItem> listOfRestaurant = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.RecyclerView);
        listViewViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(ListViewViewModel.class);

        mAdapter = new ListViewFragmentAdapter(listOfRestaurant, listOfUserWhoChose);
        mRecyclerView.setAdapter(mAdapter);
        listViewViewModel.getNearbySearchResultFromVM().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override                                    //TODO Set adapter twice ? No problem ?
            public void onChanged(NearbySearch nearbySearch) {
                listOfRestaurant.clear();
                listOfRestaurant.addAll(nearbySearch.getResults());
                mAdapter.notifyDataSetChanged();
                listViewViewModel.getListOfUsersWhoChoseRestaurant().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        listOfUserWhoChose.clear();
                        listOfUserWhoChose.addAll(users);
                        mAdapter.notifyDataSetChanged(); // TODO don't work for update number of coworker when someone change eatingPlace
                    }
                });
            }
        });

        return root;
    }
}
