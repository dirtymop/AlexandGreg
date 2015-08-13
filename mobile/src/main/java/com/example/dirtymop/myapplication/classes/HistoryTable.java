package com.example.dirtymop.myapplication.classes;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Date;
import java.util.Map;

/**
 * Created by dirtymop on 8/10/2015.
 */
@DynamoDBTable(tableName = "HistoryTable")
public class HistoryTable{
    private String FacebookID;
    private String CustomerName;
    private String latsandlong;
    private String date;
    private String time;
    private String elevation;
    private String avgspeed;
    private String distance;
    private String identify;
    private String markers;
    private String time_started;
    private String top_speed;

    // Empty constructor
    public HistoryTable() {
        super();
    }

    // Constructor with arguments
    public HistoryTable(String FacebookID,
                        String CustomerName,
                        String latsandlong,
                        String date,
                        String time,
                        String elevation,
                        String avgspeed,
                        String distance,
                        String identify,
                        String markers,
                        String time_started,
                        String top_speed) {
        super();

        // Initialize values
        this.FacebookID = FacebookID;
        this.CustomerName = CustomerName;
        this.latsandlong = latsandlong;
        this.date = date;
        this.time = time;
        this.elevation = elevation;
        this.distance=distance;
        this.markers=markers;
        this.identify=identify;
        this.avgspeed = avgspeed;
        this.top_speed= top_speed;
        this.time_started=time_started;
    }

    @DynamoDBRangeKey(attributeName ="FacebookID")
    public String getFacebookID() {
        return FacebookID;
    }
    @DynamoDBHashKey(attributeName = "CustomerName")
    public String getCustomerName() {
        return CustomerName;
    }
    @DynamoDBAttribute(attributeName = "latsandlong")
    public String getLatsandlong() {
        return latsandlong;
    }
    @DynamoDBAttribute(attributeName = "avgspeed")
    public String getAvgspeed(){
        return avgspeed;
    }
    @DynamoDBAttribute(attributeName = "elevation")
    public String getElevation(){
        return elevation;
    }
    @DynamoDBAttribute(attributeName = "time")
    public String getTime(){
        return time;
    }
    @DynamoDBAttribute(attributeName = "date")
    public String getDate(){
        return date;
    }
    @DynamoDBAttribute(attributeName = "distance")
    public String getDistance(){return distance;}
    @DynamoDBAttribute(attributeName = "identify")
    public String getIdentify(){return identify;}
    @DynamoDBAttribute(attributeName = "markers")
    public String getMarkers(){return markers;}
    @DynamoDBAttribute(attributeName = "time_started")
    public String getTime_started(){return time_started;}
    @DynamoDBAttribute(attributeName = "top_speed")
    public String getTop_speed(){return top_speed;}

    public void setTime_started(String Time_started)
    {
        this.time_started=Time_started;
    }
    public void setTop_speed(String Top_speed)
    {
        this.top_speed=Top_speed;
    }
    public void setFacebookID(String setFacebookID)
    {
        this.FacebookID=setFacebookID;
    }
    public void setCustomerName(String setCustomerName)
    {
        this.CustomerName=setCustomerName;
    }
    public void setlatsandlong(String latsandlong)
    {
        this.latsandlong=latsandlong;
    }

    public void setDate(String Date)
    {
        this.date=Date;
    }
    public void setTime(String time) {
        this.time=time;

    }
    public void setElevation(String Elevation)
    {
        this.elevation=Elevation;
    }
    public void setAvgspeed(String Avgspeed)
    {
        this.avgspeed=Avgspeed;
    }
    public void setDistance(String distance) {this.distance=distance;}
    public void setIdentify(String Identify) {this.identify=Identify;}
    public void setMarkers(String Markers){this.markers=Markers;}

}