package com.example.pokemongolocations;

// Custom class for the Adapter to fill the ListView with the PokemonData
public class PokemonData {
    private Integer pokemonId;
    private String name;
    private String imageUrl;
    private Double longitude;
    private Double latitude;

    // Constructor for building the pokemon object
    public PokemonData(Integer id, String name, String imageUrl, Double longitude, Double latitude){
        this.pokemonId = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters for private vars
    public Integer getPokemonId() {
        return pokemonId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }


}
