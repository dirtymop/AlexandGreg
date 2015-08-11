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

}