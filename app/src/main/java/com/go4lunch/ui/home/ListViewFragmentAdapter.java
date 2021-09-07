package com.go4lunch.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.databinding.FragmentListViewItemBinding;
import com.go4lunch.di.DI;
import com.go4lunch.model.nearbysearch.NearbySearch;
import com.go4lunch.model.nearbysearch.ResultsItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ListViewFragmentAdapter extends RecyclerView.Adapter<ListViewFragmentAdapter.ViewHolder> {

    private FragmentListViewItemBinding binding;
    private List<ResultsItem> listOfRestaurants;
    View itemView;

    public ListViewFragmentAdapter(List<ResultsItem> listOfRestaurants) {
        this.listOfRestaurants = listOfRestaurants;
    }

    @NonNull
    @NotNull
    @Override
    public ListViewFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListViewFragmentAdapter.ViewHolder holder, int position) {
        listOfRestaurants = Objects.requireNonNull(DI.getGooglePlaceRepository().getNearbySearchResult().getValue()).getResults();
        binding.restaurantName.setText(listOfRestaurants.get(0).getName());
    }

    @Override
    public int getItemCount() {
        return listOfRestaurants.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
