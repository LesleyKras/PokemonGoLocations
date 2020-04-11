package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class LocationService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_form);
    }

    private void back() {
        this.finish();
    }
}
