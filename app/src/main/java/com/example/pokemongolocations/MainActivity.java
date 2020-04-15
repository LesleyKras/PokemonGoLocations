package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button foundPokemonButton;
    private Button listViewButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loading settings configuration through sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer backgroundColor = sharedPreferences.getInt("background_color", Color.parseColor("#ffffff"));
        Integer buttonColor = sharedPreferences.getInt("button_color", Color.parseColor("#ffffff"));

        // "I found a cool pokemon" button from view into variable
        foundPokemonButton = (Button) findViewById(R.id.I_found_pokemon_button);
        // "Show my pokemon" button from view into variable
        listViewButton = (Button) findViewById(R.id.list_view_button);

        // Apply color settings to buttons
        foundPokemonButton.setBackgroundColor(buttonColor);
        listViewButton.setBackgroundColor(buttonColor);

        // Apply background color settings to activity
        View root = foundPokemonButton.getRootView();
        root.setBackgroundColor(backgroundColor);

        // Apply onClickListener to "I found a cool pokemon" button to open form
        foundPokemonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFormActivity();
            }
        });

        // Apply onClickListener to "Show my pokemon" button to open ListView
        listViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListViewActivity();
            }
        });
    }

    // Load menu layout into activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Add EventListener for selecting items from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switch case to handle selected menu items
        switch (item.getItemId()) {
            // Open settings activity
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Function for opening the FormActivity
    private void openFormActivity() {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    // Function for opening the ListActivity
    private void openListViewActivity() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


}
