package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button foundPokemonButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foundPokemonButton = (Button) findViewById(R.id.I_found_pokemon_button);

        foundPokemonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationActivity();
            }
        });
    }

    private void openLocationActivity() {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }


}
