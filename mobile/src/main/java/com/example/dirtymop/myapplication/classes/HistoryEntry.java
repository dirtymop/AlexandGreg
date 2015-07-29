package com.example.dirtymop.myapplication.classes;

/**
 * Class for each history entry.
 */
public class HistoryEntry {

    // Data variables
    // TODO: update fields as needed.
    public float distance;
    public float heartrate;
    public float speed;
    public float elevation;
    public String map;
    public float latitude, longitude;


    // Constructor
    public HistoryEntry(String map, float latitude, float longitude, float distance, float heartrate, float speed, float elevation) {
        this.map = map;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.heartrate = heartrate;
        this.speed = speed;
        this.elevation = elevation;
    }

}
