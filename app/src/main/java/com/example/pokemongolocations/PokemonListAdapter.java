package com.example.pokemongolocations;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PokemonListAdapter extends RecyclerView.Adapter<PokemonListAdapter.ViewHolder>{
    private ArrayList<PokemonData> listdata;
    private Context context;

    // Constructor for the Adapter
    public PokemonListAdapter(Context applicationContext, ArrayList<PokemonData> listdata) {
        // Set listData, arrayList filled with PokemonData objects
        this.listdata = listdata;
        // Set context to use functions from ListActivity
        this.context = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create LayoutInflater
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Create
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    // If listItem shows on screen, add pokemon data to listItem
    // (Recycling the listItem views for better performance / memory usage)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PokemonData pokemonData = listdata.get(position);

        // Load name and image
        holder.textView.setText(listdata.get(position).getName());
        Picasso.get().load(listdata.get(position).getImageUrl()).centerInside().resize(900,900).into(holder.imageView);

        // Add onClickListener to listItems to show pokemon location on click
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng location = new LatLng(pokemonData.getLatitude(), pokemonData.getLongitude());
                ((ListActivity) context).goToPokemonLocation(location, pokemonData.getName());
            }
        });
    }

    // Return amount of pokemons in listdata
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    // Define view class for Pokemon content
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pokemonImage);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}