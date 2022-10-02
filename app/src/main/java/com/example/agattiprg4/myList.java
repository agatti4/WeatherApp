package com.example.agattiprg4;

public class myList {
    private int value;
    private Object myList = null;
    private String timePeriodFinal;
    private int temperatureFinal;
    private String descFinal;


    /**
     * Constructor for a myList object
     * @param value
     * @param timePeriodFinal
     */
    public myList(int value, String timePeriodFinal, int temperatureFinal, String descFinal) {
        this.value = value;
        this.timePeriodFinal = timePeriodFinal;
        this.temperatureFinal = temperatureFinal;
        this.descFinal = descFinal;
    }

    /**
     * Copy constructor
     * @param toCopy
     */
    public myList(myList toCopy) {
        myList = toCopy;
        value = toCopy.getValue();
        timePeriodFinal = toCopy.getTimePeriodFinal();
        temperatureFinal = toCopy.getTemperatureFinal();
        descFinal = toCopy.getDescFinal();
    }

    /**
     * Getter for index of list object
     * @return
     */
    public int getValue() { return this.value;}

    public Object getMyList() {
        return myList;
    }

    public String getTimePeriodFinal() {
        return timePeriodFinal;
    }

    public int getTemperatureFinal() {
        return temperatureFinal;
    }

    public String getDescFinal() {
        return descFinal;
    }
}
