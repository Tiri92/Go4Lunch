package com.go4lunch.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.R;
import com.go4lunch.model.nearbysearch.ResultsItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListViewFragmentAdapter extends RecyclerView.Adapter<ListViewFragmentAdapter.ViewHolder> {

    View itemView;
    private List<ResultsItem> listOfRestaurants;
    private TextView nameOfRestaurant;
    private TextView openingHour;

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
        nameOfRestaurant = itemView.findViewById(R.id.restaurant_name);
        nameOfRestaurant.setText(listOfRestaurants.get(position).getName());
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
