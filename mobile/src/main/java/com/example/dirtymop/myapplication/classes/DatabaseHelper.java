package com.example.dirtymop.myapplication.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;

/**
 * Created by lndsharkfury on 8/7/15.
 */
public class DatabaseHelper {

    private Context context;

    // Table names
    static final String TABLE_USER = "User";
    static final String TABLE_ROUTES = "Routes";
    static final String TABLE_HISTORY = "History";
    static final String TABLE_CONTACTS = "Contacts";
    static final String TABLE_PREFERENCES = "Preferences";

    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_USER = "_user";

    // CONTACTS column names
    private static final String KEY_CONTACTS_NAME = "_name";
    private static final String KEY_CONTACTS_NUMBER = "_number";
    private static final String KEY_CONTACTS_EMAIL = "_email";

    //History Entry column names
    private static final String KEY_HISTORY_Avgspeed="Avgspeed";
    private static final String KEY_HISTORY_Date="Date";
    private static final String KEY_HISTORY_Elevation="Elevation";
    private static final String KEY_HISTORY_latsandlong="Latsandlong";
    private static final String KEY_HISTORY_TIME="Time";

    //Preferences Entry column names
    private  static final String KEY_PREFERENCES_Units="Units";



    // Table create statments
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONTACTS
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_USER + " TEXT NOT NULL, "
            + KEY_CONTACTS_NAME + " TEXT NOT NULL, "
            + KEY_CONTACTS_NUMBER + " TEXT NOT NULL, "
            + KEY_CONTACTS_EMAIL + " TEXT NOT NULL, "
            + "UNIQUE(" + KEY_CONTACTS_NAME + ")"
            + ");";

    // Constructor
    public DatabaseHelper(Context context) {
        super();
        // Set the context.
        this.context = context;
    }

    // Create the database
    public SQLiteDatabase databaseOpenOrCreate(String filename) {
        return this.context.openOrCreateDatabase(filename, this.context.MODE_PRIVATE, null);
    }

    // Create database tables
    public void createTables(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CONTACTS);
        } catch (SQLException e) {
            Log.d("db","[exception] tables were not created: " + e.getMessage());
        }
    }

    /* DB insertion methods */
    public void insertContact(SQLiteDatabase db, Contact contact, String user) {
        try {
            // Query to insert a new contact to the database.
            String insertQuery = "INSERT INTO "
                    + TABLE_CONTACTS
                    + " ( "
                    + KEY_USER + ","
                    + KEY_CONTACTS_NAME + ","
                    + KEY_CONTACTS_NUMBER + ","
                    + KEY_CONTACTS_EMAIL
                    + " )"
                    + " Values ( '" + user + "', '"
                    + contact.name + "', '"
                    + contact.number + "', '"
                    + contact.email
                    + "' )";

            // Run the SQL command.
            db.execSQL(insertQuery);
            Log.d("db", "[insert] new contact: " + insertQuery);
        } catch (SQLException e) {
            Log.d("db", "[exception] insert contact failed: " + e.getMessage());
        }
    }

    public void insertHistoryEntry(SQLiteDatabase db, HistoryTable x)
    {// KEY_ID, KEY_USER, KEY_HISTORY_Avgspeed, KEY_HISTORY_Elevation, KEY_HISTORY_latsandlong
        try {
            // Query to insert a new contact to the database.
            String insertQuery = "INSERT INTO "
                    + TABLE_HISTORY
                    + " ( "
                    + KEY_ID + ","
                    + KEY_USER + ","
                    + KEY_HISTORY_Avgspeed + ","
                    + KEY_HISTORY_Elevation + ","
                    +KEY_HISTORY_Date + ","
                    +KEY_HISTORY_TIME + ","
                    + KEY_HISTORY_latsandlong
                    + " )"
                    + " Values ( '" + x.getFacebookID() + "', '"
                    + x.getCustomerName() + "', '"
                    + x.getAvgspeed() + "', '"
                    + x.getDate() +"', '"
                    + x.getTime() +"', '"
                    + x.getLatsandlong()
                    + "' )";

            // Run the SQL command.
            db.execSQL(insertQuery);
            Log.d("db", "[insert] new Historyentry: " + insertQuery);
        } catch (SQLException e) {
            Log.d("db", "[exception] insert History entry failed: " + e.getMessage());
        }





    }
    public boolean saveContactsTable ()
    {return true;}
    public ContactsTable getContact(SQLiteDatabase db) {
        ContactsTable entry = new ContactsTable();
        String[] columns = { KEY_ID, KEY_USER,KEY_CONTACTS_NAME, KEY_CONTACTS_NUMBER, KEY_CONTACTS_EMAIL};
        Cursor c = db.query(TABLE_CONTACTS, columns, null, null, null, null, null);
        while(!c.isLast()) {
            c.moveToNext();

            entry.setFacebookID(c.getString(0));
            entry.setCustomerName(c.getString(1));
            entry.setName(c.getString(2));
            entry.setNumber(c.getString(3));
            entry.setEmail(c.getString(4));

            Log.d("db", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
        }
        return entry;
    }
    public PreferencesTable getPreferencesEntry(SQLiteDatabase db){
        PreferencesTable entry = new PreferencesTable();

    String[] columns = { KEY_ID, KEY_USER, KEY_PREFERENCES_Units};
    Cursor c = db.query(TABLE_HISTORY, columns, null, null, null, null, null);
    while(!c.isLast()) {
        c.moveToNext();

        entry.setFacebookID(c.getString(0));
        entry.setCustomerName(c.getString(1));
        entry.setUnits(c.getString(2));


        Log.d("db_GetHistoryEntry", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
    }
    return entry;
}
    public ArrayList<HistoryTable> getHistoryEntry(SQLiteDatabase db){
       ArrayList<HistoryTable> completehistory=new ArrayList<HistoryTable>();


        String[] columns = { KEY_ID, KEY_USER, KEY_HISTORY_Avgspeed,KEY_HISTORY_Date,KEY_HISTORY_TIME, KEY_HISTORY_Elevation, KEY_HISTORY_latsandlong};
        Cursor c = db.query(TABLE_HISTORY, columns, null, null, null, null, null);
        while(!c.isLast()) {
            c.moveToNext();
            HistoryTable entry = new HistoryTable();
            entry.setFacebookID(c.getString(0));
            entry.setCustomerName(c.getString(1));
            entry.setAvgspeed(c.getString(2));
            entry.setDate(c.getString(3));
            entry.setTime(c.getString(4));
            entry.setElevation(c.getString(5));
            entry.setlatsandlong(c.getString(6));

            Log.d("db_GetHistoryEntry", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
           completehistory.add(entry);
        }
        return completehistory;
    }

    public void Savetothecloud(ArrayList<HistoryTable> ht,ContactsTable ct, PreferencesTable pt)
    {

        while(!ht.isEmpty()) {
            new Saveahistoryentry(this.context).execute(ht.remove(0));
        }
        new SaveaContactsentry(this.context).execute(ct);
        new SaveaPerferencesentry(this.context).execute(pt);


    }
    private class Saveahistoryentry extends AsyncTask<HistoryTable, Integer, Integer> {
        private Context context;
        public Saveahistoryentry(Context context) {
            super();

            this.context = context;
        }

        //not gui thread
        @Override
        protected Integer doInBackground(HistoryTable... params) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    this.context,    /* get the context for the application */
                    "us-east-1:39061ec6-5149-43f4-904f-d3f14799bc63",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            HistoryTable entry=params[0];
            mapper.save(entry);


            return null;
        }


    }
    private class SaveaContactsentry extends AsyncTask<ContactsTable, Integer, Integer>
    {
        private Context context;
        public SaveaContactsentry(Context context) {
            super();

            this.context = context;
        }

        //not gui thread
        @Override
        protected Integer doInBackground(ContactsTable... params) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    this.context,    /* get the context for the application */
                    "us-east-1:39061ec6-5149-43f4-904f-d3f14799bc63",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            ContactsTable entry=params[0];
            mapper.save(entry);


            return null;
        }


    }
    private class SaveaPerferencesentry extends AsyncTask<PreferencesTable, Integer, Integer>
    {
        private Context context;
        public SaveaPerferencesentry(Context context) {
            super();

            this.context = context;
        }

        //not gui thread
        @Override
        protected Integer doInBackground(PreferencesTable... params) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    this.context,    /* get the context for the application */
                    "us-east-1:39061ec6-5149-43f4-904f-d3f14799bc63",    /* Identity Pool ID */
                    Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            PreferencesTable entry=params[0];
            mapper.save(entry);


            return null;
        }


    }


}

