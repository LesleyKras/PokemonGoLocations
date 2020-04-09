package com.example.pokemongolocations;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LocationActivity extends AppCompatActivity {
    private Button backButton;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        backButton = (Button) findViewById(R.id.back_button);
        submitButton = (Button) findViewById(R.id.submit_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hier komt de actie om data op te slaan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void back() {
        this.finish();
    }
}
