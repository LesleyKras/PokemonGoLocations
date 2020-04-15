package com.example.pokemongolocations;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SharedPreferences sharedPreferences;
    private RecyclerView recycler;
    private JSONArray savedPokemons;
    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private Integer mapZoom;
    private ArrayList<PokemonData> pokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Retrieve saved pokemons from sharedPreferences
        SharedPreferences pref = getSharedPreferences("pokemons", MODE_PRIVATE);
        String json_array = pref.getString("pokemons", null);

        // If succesfully retrieved string, create JSONArray
        if(json_array != null){
            try {
                savedPokemons = new JSONArray(json_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            // Else create empty JSONArray
            savedPokemons = new JSONArray();
        }

        // Configure actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Saved Pokémons");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Loading settings configuration for styling settings
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer backgroundColor = sharedPreferences.getInt("background_color", Color.parseColor("#ffffff"));
        mapZoom = Integer.parseInt(sharedPreferences.getString("mapZoom", "17"));

        // Load Google Maps into variable
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        // Run onMapReady() if map is loaded
        mapFragment.getMapAsync(this);

        // Put RecyclerView into variable
        recycler = (RecyclerView) findViewById(R.id.pokemonRecycler);

        // Apply background color settings to activity
        View root = recycler.getRootView();
        root.setBackgroundColor(backgroundColor);

        // Create array to place parsed pokemonData in;
        pokemonList = new ArrayList<>();

        // Parse the pokemonData retrieved from sharedPreferences
        parsePokemonData();

        // Set MyListAdapter with parsed pokemon data to the RecyclerView
        PokemonListAdapter adapter = new PokemonListAdapter(this, pokemonList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    // Function to parse the pokemonData JSONArray into array<PokemonData>
    public void parsePokemonData(){
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
    }

    // EventListener for menu items
    // For this activity; back button
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

    // Callback function if map is loaded
    // Go to start position (europe)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng fakeLocation = new LatLng(52.1950973, 5.3436271);
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(fakeLocation, 6);
        googleMap.animateCamera(camPosition);
        this.googleMap = googleMap;
    }

    // Function to go to the location of saved pokemon and place a marker
    // This function is being used in onBindViewHolder() of PokemonListAdapter in OnClickListener for list items
    public void goToPokemonLocation(LatLng location, String name) {
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name));
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(location, mapZoom);
        googleMap.animateCamera(camPosition);
    }
}
