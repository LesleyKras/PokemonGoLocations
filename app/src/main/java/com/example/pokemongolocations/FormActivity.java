package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FormActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView pokemonName;
    private ImageView pokemonImageView;
    private Spinner pokemonsSpinner;
    private Integer mapZoom;
    private ArrayList<String> pokemons =new ArrayList<>();
    private LatLng location;
    private String pokemonNameForSubmit;
    private Integer pokemonId;
    private String pokemonImageUrl;
    private JSONArray pokemonDataArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer menuColor = sharedPreferences.getInt("menu_color", Color.parseColor("#ffffff"));
        Integer backgroundColor = sharedPreferences.getInt("background_color", Color.parseColor("#ffffff"));
        Integer buttonColor = sharedPreferences.getInt("button_color", Color.parseColor("#ffffff"));

        // Set title for activity
        setTitle("Register your pokémon");

        // Put elements from layout into variables
        Button backButton = (Button) findViewById(R.id.back_button);
        Button submitButton = (Button) findViewById(R.id.submit_button);
        pokemonsSpinner = (Spinner) findViewById(R.id.pokemons_spinner);
        pokemonImageView = (ImageView) findViewById(R.id.pokemon_image_view);
        pokemonName = (TextView) findViewById(R.id.pokemon_name);

        SharedPreferences pref = getSharedPreferences("pokemons", MODE_PRIVATE);
        String json_array = pref.getString("pokemons", null);
        if(json_array != null){
            try {
                pokemonDataArray = new JSONArray(json_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            pokemonDataArray = new JSONArray();
        }

        //Set colors from settings
        backButton.setBackgroundColor(buttonColor);
        submitButton.setBackgroundColor(buttonColor);
        pokemonImageView.setBackgroundColor(backgroundColor);

        //Apply background color settings to activity
        View root = backButton.getRootView();
        root.setBackgroundColor(backgroundColor);

        //Google Maps settings
        mapZoom = Integer.parseInt(sharedPreferences.getString("mapZoom", "17"));

        // Load pokemon data from API into the spinner; limit=964 for all pokémons
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
                try {
                    submitPokemonData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Event listener if Item from spinner gets selected
        pokemonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pokemonId = pokemonsSpinner.getSelectedItemPosition() + 1;
                getPokemonDataById(pokemonId);
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
                                Integer pokemonId = i + 1;
                                String pokemonIDString = "#" + pokemonId + " | ";
                                String pokemonName = pokemonObject.getString("name");
                                pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1).toLowerCase();
                                pokemonName = pokemonIDString + pokemonName;

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
                        pokemonNameForSubmit = name;
                        pokemonImageUrl = pokemonSprite;
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

    private void submitPokemonData() throws JSONException {
        JSONObject pokemonData = new JSONObject();
        try{
            pokemonData.put("id", pokemonId);
            pokemonData.put("name", pokemonNameForSubmit );
            pokemonData.put("image_url", pokemonImageUrl);
            pokemonData.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pokemonDataArray.put(pokemonData);

        String pokemonsDataString = pokemonDataArray.toString();

        getSharedPreferences("pokemons", MODE_PRIVATE)
                .edit()
                .putString("pokemons", pokemonsDataString)
                .apply();
        Toast.makeText(getApplicationContext(), "Pokemon and location successfully saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationService locationService = new LocationService();
        location = locationService.getLatLng();

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Marker"));
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(location, mapZoom);
        googleMap.animateCamera(camPosition);
    }

    // Back functionality for back button
    private void back() {
        this.finish();
    }
}