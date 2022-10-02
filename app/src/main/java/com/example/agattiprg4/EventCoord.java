package com.example.agattiprg4;

/**
 * {@Event} represents a weather event. It holds the details
 * of that event such as name of city, longitude, and latitude
 */
public class EventCoord {

    /** Name of the city from zipcode */
    public final String nameOfCity;

    /** Longitude of the zipcode */
    public final int longt;

    //latitude of the zipcode
    public final int lat;


    /**
     * Constructs a new {@link EventCoord}.
     *
     * @param eventNameOfCity is the name of the city pulled from the zipcode
     * @param eventLongt is the time period of the weather
     * @param eventLat is the temperature of the current period
     */
    public EventCoord(String eventNameOfCity, int eventLongt, int eventLat) {
        nameOfCity = eventNameOfCity;
        longt = eventLongt;
        lat = eventLat;
    }
}