package com.example.dirtymop.myapplication.classes;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by dirtymop on 8/10/2015.
 */
@DynamoDBTable(tableName = "PreferencesTable")
public class PreferencesTable{
    private String FacebookID;
    private String CustomerName;
    private String units;

    @DynamoDBRangeKey(attributeName ="FacebookID")
    public String getFacebookID() {
        return FacebookID;
    }

    @DynamoDBHashKey(attributeName = "CustomerName")
    public String getCustomerName() {
        return CustomerName;
    }
    @DynamoDBAttribute(attributeName = "Units")
    public String getUnits() {
        return units;
    }
   public  void setFacebookID(String setFacebookID)
    {
        this.FacebookID=setFacebookID;
    }
    public void setCustomerName(String setCustomerName)
    {
        this.CustomerName=setCustomerName;
    }
    public void setUnits(String units)
    {
        this.units=units;
    }

}