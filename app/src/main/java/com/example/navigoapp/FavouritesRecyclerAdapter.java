package com.example.navigoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesRecyclerAdapter extends RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Favourites> favouritesList;

    public FavouritesRecyclerAdapter(Context context, List<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourites_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favourites favourites = favouritesList.get(position);

        holder.placeName.setText(favourites.getPlaceName());
        holder.placeAddress.setText(favourites.getPlaceAddress());
        holder.placeLat.setText(favourites.getPlaceLat());
        holder.placeLng.setText(favourites.getPlaceLng());
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView placeName;
        TextView placeAddress;
        TextView placeLat;
        TextView placeLng;
        String userId;

        public ViewHolder(View view, Context ctx){
            super(view);
            context = ctx;

            placeName = (TextView) view.findViewById(R.id.place_name);
            placeAddress = (TextView) view.findViewById(R.id.place_address);
            placeLat = (TextView) view.findViewById(R.id.place_lat);
            placeLng = (TextView) view.findViewById(R.id.place_lng);

            userId = null;
        }

    }
}
