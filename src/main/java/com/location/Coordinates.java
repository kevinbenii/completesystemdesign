package com.location;

/** Latitude and longitude */
public class Coordinates {
    private double latitude;
    private double longitude;

    /** Makes coordinates */
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** Latitude */
    public double getLatitude() {
        return latitude;
    }

    /** Longitude */
    public double getLongitude() {
        return longitude;
    }

    @Override
    /** Latitude, longitude */
    public String toString() {
        return latitude + ", " + longitude;
    }
}
