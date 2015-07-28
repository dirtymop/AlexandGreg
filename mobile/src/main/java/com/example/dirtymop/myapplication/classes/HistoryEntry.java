package com.example.dirtymop.myapplication.classes;

/**
 * Class for each history entry.
 */
public class HistoryEntry {

    // Data variables
    // TODO: update fields as needed.
    public float distance;
    public float calories;
    public float speed;
    public String map;
    public float latitude, longitude;


    // Constructor
    public HistoryEntry(String map, float latitude, float longitude, float distance, float calories, float speed) {
        this.map = map;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.calories = calories;
        this.speed = speed;
    }

}
