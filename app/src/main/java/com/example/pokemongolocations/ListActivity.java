package com.example.pokemongolocations;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray savedPokemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Retrieve saved pokemons from sharedPreferences
        SharedPreferences pref = getSharedPreferences("pokemons", MODE_PRIVATE);
        String json_array = pref.getString("pokemons", null);
        if(json_array != null){
            try {
                savedPokemons = new JSONArray(json_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            savedPokemons = new JSONArray();
        }

        //Configure actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Saved Pokémons");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Loading settings configuration for styling settings
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer menuColor = sharedPreferences.getInt("menu_color", Color.parseColor("#ffffff"));
        Integer backgroundColor = sharedPreferences.getInt("background_color", Color.parseColor("#ffffff"));
        Integer buttonColor = sharedPreferences.getInt("button_color", Color.parseColor("#ffffff"));

        recycler = (RecyclerView) findViewById(R.id.pokemonRecycler);

        //Apply background color settings to activity
        View root = recycler.getRootView();
        root.setBackgroundColor(backgroundColor);

        ArrayList<PokemonData> pokemonList = new ArrayList<PokemonData>();
        for (int i =0; i < savedPokemons.length(); i++){
            try {
                JSONObject pokemon = savedPokemons.getJSONObject(i);
                Integer id = pokemon.getInt("id");
                String pokemonName = pokemon.getString("name");
                String imageUrl = pokemon.getString("image_url");
                Double longitude = pokemon.getDouble("longitude");
                Double latitude =pokemon.getDouble("latitude");

                PokemonData pokemonData = new PokemonData(id, pokemonName, imageUrl, longitude, latitude);
                pokemonList.add(pokemonData);
                Log.d("testtest", pokemonData.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        MyListAdapter adapter = new MyListAdapter(pokemonList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        Log.d("TAGGGG:", Integer.toString(savedPokemons.length()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
