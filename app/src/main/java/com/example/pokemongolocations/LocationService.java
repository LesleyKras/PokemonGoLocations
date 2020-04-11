package com.example.pokemongolocations;

import com.google.android.gms.maps.model.LatLng;

public class LocationService {
    private double longitude;
    private double latitude;

    LocationService(){
        getLocation();
    }

    private void getLocation() {
        setLongitude(4.9913655);
        setLatitude(51.8387731);
    }

    public LatLng getLatLng() {
        return new LatLng(getLatitude(),getLongitude());
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }






}
