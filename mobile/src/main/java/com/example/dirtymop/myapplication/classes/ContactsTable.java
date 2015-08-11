package com.example.dirtymop.myapplication.classes;

/**
 * Created by dirtymop on 8/10/2015.
 */
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by dirtymop on 8/10/2015.
 */
@DynamoDBTable(tableName = "ContactsTable")
public class ContactsTable{
    private String FacebookID;
    private String CustomerName;
    private String Name;
    private String Number;
    private String Email;

    @DynamoDBRangeKey(attributeName ="FacebookID")
    public String getFacebookID() {
        return FacebookID;
    }

    @DynamoDBHashKey(attributeName = "CustomerName")
    public String getCustomerName() {
        return CustomerName;
    }

    void setFacebookID(String FacebookID)
    {
        this.FacebookID=FacebookID;
    }
    void setCustomerName(String CustomerName)
    {
        this.CustomerName=CustomerName;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return Name;
    }

    @DynamoDBAttribute(attributeName = "Number")
    public String getNumber() {
        return Number;
    }

    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return Email;
    }

    void setName(String Name)
    {
        this.Name=Name;
    }
    void setNumber(String number)
    {
        this.Number=number;
    }
    void setEmail(String email)
    {
        this.Email=email;
    }

}