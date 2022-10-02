package com.example.agattiprg4;

/**
 * {@Event} represents a weather event. It holds the details
 * of that event such as time period, as well as temperature, and a description.
 */
public class Event {

    /** Time period of the weather */
    public final String timePeriod;

    /** Temperature */
    public final int temperature;

    /** Description of Weather */
    public final String description;

    /**
     * Constructs a new {@link Event}.
     *
     * @param eventTimePeriod is the time period of the weather
     * @param eventTemperature is the temperature of the current period
     * @param eventDescription is the description of the weather
     */
    public Event(String eventTimePeriod, int eventTemperature, String eventDescription) {
        timePeriod = eventTimePeriod;
        temperature = eventTemperature;
        description = eventDescription;
    }
}