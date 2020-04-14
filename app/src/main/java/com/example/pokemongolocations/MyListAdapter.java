package com.example.pokemongolocations;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<PokemonData> listdata;
    private Context context;


    // RecyclerView recyclerView;
    public MyListAdapter(Context applicationContext, ArrayList<PokemonData> listdata) {
        this.listdata = listdata;
        this.context = applicationContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PokemonData pokemonData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getName());
        Picasso.get().load(listdata.get(position).getImageUrl()).centerInside().resize(900,900).into(holder.imageView);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng location = new LatLng(pokemonData.getLatitude(), pokemonData.getLongitude());
                ((ListActivity) context).goToPokemonLocation(location, pokemonData.getName());
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

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