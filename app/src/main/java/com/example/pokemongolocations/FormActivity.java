package com.example.pokemongolocations;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FormActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Activity Elements
    private ImageView pokemonImageView;
    private TextView pokemonName;
    private Spinner pokemonsSpinner;
    private MapFragment mapFragment;

    // Pokemon Data
    private Integer pokemonId;
    private String pokemonNameForSubmit;
    private String pokemonImageUrl;

    // Settings
    private Integer mapZoom;
    private Location lastKnownLocation;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker marker;

    // Other
    private JSONArray pokemonDataArray;
    private ArrayList<String> pokemons =new ArrayList<>();
    private GoogleMap googleMap;

    private static final String ERROR_MESSAGE_RETRIEVING_DATA = "Something went wrong retrieving the Pokemon data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Retrieve settings from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer backgroundColor = sharedPreferences.getInt("background_color", Color.parseColor("#ffffff"));
        Integer buttonColor = sharedPreferences.getInt("button_color", Color.parseColor("#ffffff"));
        mapZoom = Integer.parseInt(sharedPreferences.getString("mapZoom", "17"));

        // Configure actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register your pokémon!");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Put elements from layout into variables
        Button submitButton = (Button) findViewById(R.id.submit_button);
        pokemonsSpinner = (Spinner) findViewById(R.id.pokemons_spinner);
        pokemonImageView = (ImageView) findViewById(R.id.pokemon_image_view);
        pokemonName = (TextView) findViewById(R.id.pokemon_name);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        // Create locationRequest and Callback for updating current location
        locationRequest = createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // On new location, remove old marker if present and place a new one
                    if(marker != null){
                        marker.remove();
                    }
                    // Trigger updateLocation() on new location
                    updateLocation();
                }
            }
        };

        // Load currently saved pokemons to extend JSONArray
        SharedPreferences pref = getSharedPreferences("pokemons", MODE_PRIVATE);
        String json_array = pref.getString("pokemons", null);

        // Check if pokemon data is available
        if (json_array != null) {
            try {
                pokemonDataArray = new JSONArray(json_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Else create a new empty array
        } else {
            pokemonDataArray = new JSONArray();
        }

        //Set button colors from settings
        submitButton.setBackgroundColor(buttonColor);
        pokemonImageView.setBackgroundColor(backgroundColor);

        //Apply background color settings to activity
        View root = submitButton.getRootView();
        root.setBackgroundColor(backgroundColor);

        // Load pokemon data from API into the spinner; limit=964 for all pokémons
        getPokemonsForSpinner();

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
                // Get pokemon detail data
                getPokemonDataById(pokemonId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Make instance of the fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request lastknownLocation and load Google map
        getLastLocation();

        // Start location updates
        startLocationUpdates();
    }

    // EventListener for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu items
        switch (item.getItemId()) {
            // Case for back button
            case R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Make API Call to retrieve pokemonData and put it into Spinner
    private void getPokemonsForSpinner(){
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        // Create volley requestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            // Create JSONObject request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject object = response;
                        // Get JsonArray of all pokemon
                        JSONArray pokemonArray = object.getJSONArray("results");
                        //If array is filled
                        if (pokemonArray.length() > 0) {
                            // Loop through array to get data for each pokemon
                            for (int i = 0; i < pokemonArray.length(); i++) {
                                JSONObject pokemonObject = pokemonArray.getJSONObject(i);
                                Integer pokemonId = i + 1;
                                String pokemonIDString = "#" + pokemonId + " | ";
                                String pokemonName = pokemonObject.getString("name");

                                //Build string for Spinner
                                pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1).toLowerCase();
                                pokemonName = pokemonIDString + pokemonName;

                                //put pokemon string in array for use of Spinner
                                pokemons.add(pokemonName);
                            }
                        } else {
                            //Show error on error retrieving data
                            Toast.makeText(getApplicationContext(), ERROR_MESSAGE_RETRIEVING_DATA, Toast.LENGTH_SHORT).show();
                        }
                        //Load pokemon data with ArrayAdapter<String> to load ArrayList<String> pokemons
                        pokemonsSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pokemons));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Show error on error retrieving data
                    Toast.makeText(getApplicationContext(), ERROR_MESSAGE_RETRIEVING_DATA, Toast.LENGTH_SHORT).show();
                }
            });
            //Add jsonObjectRequest to volley queue
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function for getting detail data for selected pokemon
    private void getPokemonDataById(int id){
        String url = "https://pokeapi.co/api/v2/pokemon/" + id;
        // Create volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Put response into JsonObject
                        JSONObject object = response;
                        // Get image url
                        String pokemonSprite = object.getJSONObject("sprites").getString("front_default");
                        // Parse pokemon name
                        String name = object.getString("name");
                        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        // Set pokemonData to elements in activity
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
                    Toast.makeText(getApplicationContext(), ERROR_MESSAGE_RETRIEVING_DATA, Toast.LENGTH_SHORT).show();
                }
            });
            // Add request to queue
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function for saving the pokemonData + found location
    private void submitPokemonData() throws JSONException {
        // Create JsonObject to build pokemon data
        JSONObject pokemonData = new JSONObject();
        try{
            pokemonData.put("id", pokemonId);
            pokemonData.put("name", pokemonNameForSubmit );
            pokemonData.put("image_url", pokemonImageUrl);
            pokemonData.put("longitude", lastKnownLocation.getLongitude());
            pokemonData.put("latitude", lastKnownLocation.getLatitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Put created pokemon data into JSONArray
        pokemonDataArray.put(pokemonData);

        // Parse JSONArray to String
        String pokemonsDataString = pokemonDataArray.toString();

        // Save new string to sharedPreferences
        getSharedPreferences("pokemons", MODE_PRIVATE)
                .edit()
                .putString("pokemons", pokemonsDataString)
                .apply();
        Toast.makeText(getApplicationContext(), "Pokemon and location successfully saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    //Function for initialising the map with last known location
    public void getLastLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lastKnownLocation = location;
                            loadMap();
                        }
                    }
                });
    }

    // Function triggered when a new location has been found
    public void updateLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // If location != null, update lastKnownLocation
                            lastKnownLocation = location;
                            // If google maps has loaded and is ready move camera to new location and place marker
                            if (googleMap != null){
                                CameraUpdate camPosition = CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                                googleMap.animateCamera(camPosition);
                                marker = googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                                        .title("Current Location"));
                            }
                        }
                    }
                });
    }

    //Function to trigger onMapReadyCallback()
    public void loadMap(){
        mapFragment.getMapAsync(this);
    }

    //Function to create a LocationRequest
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    // Start requesting location based on settings from LocationRequest with callback function locationCallback
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    // If map is ready, go to last known position with zoom from settings
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), mapZoom);
        googleMap.animateCamera(camPosition);
    }

    // If app life cycle is resumed, start requesting location updates
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    // If app life cycle is on pause, stop asking for location updates
    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}