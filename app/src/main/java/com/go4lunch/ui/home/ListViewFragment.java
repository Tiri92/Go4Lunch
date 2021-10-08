package com.go4lunch.ui.home;

import static com.go4lunch.ui.home.MapViewFragment.myPosition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.model.details.DetailSearch;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.NearbySearch;
import com.go4lunch.model.nearbysearch.ResultsItem;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    public ListViewViewModel listViewViewModel;
    private RecyclerView mRecyclerView;
    ListViewFragmentAdapter mAdapter;
    ListViewFragmentAdapterVac mAdapterVac;
    private final List<ResultsItem> listOfRestaurant = new ArrayList<>();
    private final List<DetailSearch> listOfRestaurantVac = new ArrayList<>();
    public List<User> listOfUserWhoChose = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.RecyclerView);
        listViewViewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(ListViewViewModel.class);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.i_m_hungry));

        mAdapter = new ListViewFragmentAdapter(listOfRestaurant, listOfUserWhoChose);
        mAdapterVac = new ListViewFragmentAdapterVac(listOfRestaurantVac, listOfUserWhoChose);
        mRecyclerView.setAdapter(mAdapter);
        listViewViewModel.getNearbySearchResultFromVM().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override
            public void onChanged(NearbySearch nearbySearch) {
                listOfRestaurant.clear();
                listOfRestaurant.addAll(nearbySearch.getResults());
                mAdapter.notifyDataSetChanged();
                listViewViewModel.getListOfUsersWhoChoseRestaurant().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        listOfUserWhoChose.clear();
                        listOfUserWhoChose.addAll(users);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_a_restaurant));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (myPosition != null) {
                    listViewViewModel.callAutocompleteSearch(MapViewFragment.myPosition.latitude + "," + myPosition.longitude, query);
                    setupObserver();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setupObserver() {
        listViewViewModel.getAutocompleteSearchResultFromVM().observe(getViewLifecycleOwner(), new Observer<List<DetailSearch>>() {
            @Override
            public void onChanged(List<DetailSearch> detailSearches) {
                listOfRestaurantVac.clear();
                listOfRestaurantVac.addAll(detailSearches);
                mRecyclerView.setAdapter(mAdapterVac);
            }
        });

    }


}
