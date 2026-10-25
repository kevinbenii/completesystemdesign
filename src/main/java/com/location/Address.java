package com.location;


/** Street address */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    /** Makes an address */
    public Address(String street, String city, String state, String zip){
        this.street = street;
        this. city = city;
        this.state = state;
        this.zip = zip;
    }

    /** Street */
    public String getStreet() {
        return street;
    }

    /** City */
    public String getCity() {
        return city;
    }

    /** State */
    public String getState() {
        return state;
    }

    /** Zip code */
    public String getZip() {
        return zip;
    }

    /** Looks up its coordinates */
    public Coordinates getCoordinates() {
        try {
            return MapTools.getInstance().getCoordinates(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Full address */
    public String toString() {
        return String.format("%s, %s, %s %s", street, city, state, zip);
    }
}
