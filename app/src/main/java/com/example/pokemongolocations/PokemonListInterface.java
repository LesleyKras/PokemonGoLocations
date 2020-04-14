package com.example.pokemongolocations;

import com.google.android.gms.maps.model.LatLng;

public interface PokemonListInterface {
    void goToPokemonLocation(LatLng location, String name);
}
