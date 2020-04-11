package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FormActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button backButton;
    private Button submitButton;
    private TextView pokemonName;
    private ImageView pokemonImageView;
    private Spinner pokemonsSpinner;
    private Integer mapZoom;
    private ArrayList<String> pokemons =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Set title for activity
        setTitle("Register your pokémon");

        // Put elements from layout into variables
        backButton = (Button) findViewById(R.id.back_button);
        submitButton = (Button) findViewById(R.id.submit_button);
        pokemonsSpinner = (Spinner) findViewById(R.id.pokemons_spinner);
        pokemonImageView = (ImageView) findViewById(R.id.pokemon_image_view);
        pokemonName = (TextView) findViewById(R.id.pokemon_name);

        //Google Maps settings
        mapZoom = 17;

        // Load pokemon data from API into the spinner; limit=964 for all pokémon
        getPokemonsForSpinner("https://pokeapi.co/api/v2/pokemon?limit=151");

        // Set EventListener for back button to return to previous activity.
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        // Set EventListener for submit button to save data on device.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hier komt de actie om data op te slaan", Toast.LENGTH_SHORT).show();
            }
        });

        // Event listener if Item from spinner gets selected
        pokemonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int id = pokemonsSpinner.getSelectedItemPosition() + 1;
                getPokemonDataById(id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getPokemonsForSpinner(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject object = response;
                        JSONArray pokemonArray = object.getJSONArray("results");

                        if (pokemonArray.length() > 0) {
                            for (int i = 0; i < pokemonArray.length(); i++) {

                                //Retrieve data from the API JSONArray
                                JSONObject pokemonObject = pokemonArray.getJSONObject(i);
                                String pokemonName = pokemonObject.getString("name");
                                pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1).toLowerCase();

                                //put pokemon name in array for use of Spinner
                                pokemons.add(pokemonName);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong retrieving the Pokemon data", Toast.LENGTH_SHORT).show();
                        }
                        pokemonsSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pokemons));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Something went wrong retrieving the Pokemon data", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPokemonDataById(int id){
        String url = "https://pokeapi.co/api/v2/pokemon/" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject object = response;
                        String pokemonSprite = object.getJSONObject("sprites").getString("front_default");
                        String name = object.getString("name");
                        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                        pokemonName.setText(name);
                        Picasso.get().load(pokemonSprite).centerInside().resize(900,900).into(pokemonImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Something went wrong retrieving the Pokemon data", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Back functionality for back button
    private void back() {
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationService locationService = new LocationService();
        LatLng location = locationService.getLatLng();

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Marker"));
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(location, mapZoom);
        googleMap.animateCamera(camPosition);
    }
}