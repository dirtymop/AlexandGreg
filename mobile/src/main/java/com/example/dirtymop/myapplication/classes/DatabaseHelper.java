package com.example.dirtymop.myapplication.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public void getContact(SQLiteDatabase db) {
        String[] columns = { KEY_USER, KEY_CONTACTS_NAME, KEY_CONTACTS_NUMBER, KEY_CONTACTS_EMAIL};
        Cursor c = db.query(TABLE_CONTACTS, columns, null, null, null, null, null);
        while(!c.isLast()) {
            c.moveToNext();

            Log.d("db", "[entry]: " + c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
        }
    }
}
