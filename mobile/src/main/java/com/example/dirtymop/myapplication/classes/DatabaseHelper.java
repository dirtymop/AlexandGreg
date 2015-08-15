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
import com.google.android.gms.maps.model.LatLng;

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
    private static final String KEY_HISTORY_DISTANCE="Distance";
    private static final String KEY_HISTORY_IDENTIFY="Identify";
    private static final String KEY_HISTORY_MARKERS="Markers";
    private static final String KEY_HISTORY_TIME_STARTED="Time_started";
    private static final String KEY_HISTORY_Top_speed="Top_speed";



    //Preferences Entry column names
    private  static final String KEY_PREFERENCES_Units="Units";

public HistoryTable theHistorytable;

    // Table create statments
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONTACTS
            + " ( "
            + KEY_ID + " TEXT PRIMARY KEY, "
            + KEY_USER + " TEXT NOT NULL, "
            + KEY_CONTACTS_NAME + " TEXT NOT NULL, "
            + KEY_CONTACTS_NUMBER + " TEXT NOT NULL, "
            + KEY_CONTACTS_EMAIL + " TEXT NOT NULL, "
            + "UNIQUE(" + KEY_CONTACTS_NAME + ")"
            + ");";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_HISTORY
            + " ( "
            + KEY_ID + " TEXT NOT NULL, "
            + KEY_USER + " TEXT NOT NULL, "
            + KEY_HISTORY_Avgspeed + " TEXT NOT NULL, "
            + KEY_HISTORY_Elevation + " TEXT NOT NULL, "
            + KEY_HISTORY_Date + " TEXT NOT NULL, "
            + KEY_HISTORY_TIME + " TEXT NOT NULL, "
            + KEY_HISTORY_IDENTIFY + " TEXT PRIMARY KEY, "
            + KEY_HISTORY_TIME_STARTED + " TEXT NOT NULL, "
            + KEY_HISTORY_Top_speed + " TEXT NOT NULL, "
            + KEY_HISTORY_MARKERS+ " TEXT NOT NULL, "
            + KEY_HISTORY_DISTANCE + " TEXT NOT NULL, "
            + KEY_HISTORY_latsandlong + " TEXT NOT NULL"
            + ");";

    // Constructor
    public DatabaseHelper(Context context) {
        super();
        // Set the context.
        this.context = context;
    }
    public ArrayList<LatLng> getlatlong(String latlongresults)
    {
        ArrayList<LatLng> locations=new ArrayList<>();
        String parts[] = latlongresults.split(";");
        int I=0;
        if (parts != null)
        {
            while(I<=parts.length)
            {
                String latandlong[]= parts[I].split(",");
                String lat=latandlong[0];
                String lng=latandlong[1];
                Double latdouble=Double.parseDouble(lat);
                Double lngdouble=Double.parseDouble(lng);
                LatLng latlongtoadd= new LatLng(latdouble,lngdouble);
                locations.add(I,latlongtoadd);
                I++;

            }
        }

        return locations;
    }
    // Create the database
    public SQLiteDatabase databaseOpenOrCreate(String filename) {
        return this.context.openOrCreateDatabase(filename, this.context.MODE_PRIVATE, null);
    }

    // Create database tables
    public void createTables(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CONTACTS);
            db.execSQL(CREATE_TABLE_HISTORY);
        } catch (SQLException e) {
            Log.d("db","[exception] tables were not created: " + e.getMessage());
        }
    }

    /*
    * LOCAL DB: Insertion methods
    *
    * */
    public void insertContact(SQLiteDatabase db, ContactsTable contact)
    {
        try {
            // Query to insert a new contact to the database.
            String insertQuery = "INSERT INTO "
                    + TABLE_CONTACTS
                    + " ( "
                    + KEY_ID + ","
                    + KEY_USER + ","
                    + KEY_CONTACTS_NAME + ","
                    + KEY_CONTACTS_NUMBER + ","
                    + KEY_CONTACTS_EMAIL
                    + " )"
                    + " Values ( '"
                    + contact.getFacebookID() + "', '"
                    + contact.getCustomerName() + "', '"
                    + contact.getName() + "', '"
                    + contact.getNumber() + "', '"
                    + contact.getEmail()
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
                    + KEY_HISTORY_Date + ","
                    + KEY_HISTORY_TIME + ","
                    + KEY_HISTORY_IDENTIFY + ","
                    + KEY_HISTORY_TIME_STARTED + ","
                    + KEY_HISTORY_Top_speed + ","
                    + KEY_HISTORY_MARKERS + ","
                    + KEY_HISTORY_DISTANCE + ","
                    + KEY_HISTORY_latsandlong
                    + " )"
                    + " Values ( '" + x.getFacebookID() + "', '"
                    + x.getCustomerName() + "', '"
                    + x.getAvgspeed() + "', '"
                    + x.getElevation() + "', '"
                    + x.getDate() +"', '"
                    + x.getTime() +"', '"
                    + x.getIdentify() +"', '"
                    + x.getTime_started() +"', '"
                    + x.getTop_speed() +"', '"
                    + x.getMarkers() +"', '"
                    + x.getDistance() +"', '"
                    + x.getLatsandlong()
                    + "' )";

            // Run the SQL command.
            db.execSQL(insertQuery);
            Log.d("db", "[insert] new Historyentry: " + insertQuery);
        } catch (SQLException e) {
            Log.d("db", "[exception] insert History entry failed: " + e.getMessage());
        }
    }

    public boolean saveContactsTable () {return true;}

    /*
    * LOCAL DB: Getter methods
    * */
    public ContactsTable getContact(SQLiteDatabase db) {

        try {
            ContactsTable entry = new ContactsTable();
            String[] columns = {KEY_ID, KEY_USER, KEY_CONTACTS_NAME, KEY_CONTACTS_NUMBER, KEY_CONTACTS_EMAIL};
            Cursor c = db.query(TABLE_CONTACTS, columns, null, null, null, null, null);

            if (c.getCount() > 0) {
                while (!c.isLast()) {
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
            else {
                return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }
    public PreferencesTable getPreferencesEntry(SQLiteDatabase db){

        try {
            PreferencesTable entry = new PreferencesTable();

            String[] columns = {KEY_ID, KEY_USER, KEY_PREFERENCES_Units};
            Cursor c = db.query(TABLE_HISTORY, columns, null, null, null, null, null);

            if (c.getCount() > 0) {
                while (!c.isLast()) {
                    c.moveToNext();

                    entry.setFacebookID(c.getString(0));
                    entry.setCustomerName(c.getString(1));
                    entry.setUnits(c.getString(2));


                    Log.d("db_GetHistoryEntry", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
                }
                return entry;
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }
    public ArrayList<HistoryTable> getHistoryEntry(SQLiteDatabase db){

        try {
            ArrayList<HistoryTable> completehistory = new ArrayList<HistoryTable>();

            String[] columns = {KEY_ID, KEY_USER, KEY_HISTORY_Avgspeed, KEY_HISTORY_Date, KEY_HISTORY_TIME, KEY_HISTORY_Elevation, KEY_HISTORY_latsandlong, KEY_HISTORY_TIME_STARTED, KEY_HISTORY_IDENTIFY, KEY_HISTORY_DISTANCE, KEY_HISTORY_Top_speed, KEY_HISTORY_MARKERS};
            Cursor c = db.query(TABLE_HISTORY, columns, null, null, null, null, null);

            Log.d("db", "cursor count: " + c.getCount());

            if (c.getCount() > 0) {
                while (!c.isLast()) {
                    c.moveToNext();
                    HistoryTable entry = new HistoryTable();
                    entry.setFacebookID(c.getString(0));
                    entry.setCustomerName(c.getString(1));
                    entry.setAvgspeed(c.getString(2));
                    entry.setDate(c.getString(3));
                    entry.setTime(c.getString(4));
                    entry.setElevation(c.getString(5));
                    entry.setlatsandlong(c.getString(6));
                    entry.setTime_started(c.getString(7));
                    entry.setIdentify(c.getString(8));
                    entry.setDistance(c.getString(9));
                    entry.setTop_speed(c.getString(10));
                    entry.setMarkers(c.getString(11));


                    Log.d("db_GetHistoryEntry", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
                    completehistory.add(entry);
                }
                return completehistory;
            }
            return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    public void Pullfromthecloud(SQLiteDatabase db)
    {
        HistoryTable entry=new HistoryTable();

      //  insertHistoryEntry(db,entry);

    }

    private class Pullfromcloud extends AsyncTask<Integer, Integer, Integer>
    {

        private Context context;
        public Pullfromcloud(Context context) {
            super();

            this.context = context;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.d("help", "inside asynctask load from server");
            HistoryTable pulled= new HistoryTable();


                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        this.context,    /* get the context for the application */
                        "us-east-1:39061ec6-5149-43f4-904f-d3f14799bc63",    /* Identity Pool ID */
                        Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
                );
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                pulled= mapper.load(HistoryTable.class, "1234567890","1234567890");

            pulled.getFacebookID();



                 //   pulled= mapper.load(HistoryTable.class, "1234567890");
              //   theHistorytable=selectedhistorytableentry;
            return null;
        }
    }
    public void Savetothecloud(ArrayList<HistoryTable> ht,ContactsTable ct, PreferencesTable pt)
    {

        while(!ht.isEmpty()) {
            new Saveahistoryentry(this.context).execute(ht.remove(0));
        }
        new SaveaContactsentry(this.context).execute(ct);
        new SaveaPerferencesentry(this.context).execute(pt);




    }
    private class Saveahistoryentry extends AsyncTask<HistoryTable, Integer, Integer>
    {
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

