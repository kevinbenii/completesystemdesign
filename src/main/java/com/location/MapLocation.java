package com.location;

/*
 * Represents one address after it has been converted
 * into latitude and longitude coordinates.
 */
public class MapLocation {

    private final Address address;
    private final Coordinates coordinates;

    /** Makes a map location */
    public MapLocation(Address address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    /** Address */
    public Address getAddress() {
        return address;
    }

    /** Coordinates */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    /** Address as text */
    public String toString() {
        return address.toString();
    }
}