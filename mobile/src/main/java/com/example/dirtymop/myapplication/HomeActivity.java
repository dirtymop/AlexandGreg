package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dirtymop.myapplication.classes.Contact;
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.fragments.History;
import com.example.dirtymop.myapplication.interfaces.HistoryInteractionListener;


public class HomeActivity extends Activity implements HistoryInteractionListener {

    private static final String TAG_FRAG_HISTORY = "history_fragment";

    private FragmentManager fm;

    private Fragment history;
    private Button selectMapButton;

    // SQLite database
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private static final String DB_FILENAME = "local.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // SQLite
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);
        dbHelper.createTables(db);
//        dbHelper.insertContact(db, new Contact("Alex", "540-419-7390", "acd1797@vt.edu"), "derieux");
        dbHelper.insertHistoryEntry(db, new HistoryTable(
                "1111",
                "1111",
                "1111",
                "1111",
                "1111",
                "1111",
                "1111"
        ));
        dbHelper.insertHistoryEntry(db, new HistoryTable(
                "2222",
                "2222",
                "2222",
                "2222",
                "2222",
                "2222",
                "2222"
        ));
//        dbHelper.getContact(db);
        for (HistoryTable table : dbHelper.getHistoryEntry(db)) {
            Log.d("home", "[entry]: " + table.getFacebookID());
        }
//        dbHelper.getHistoryEntry(db);

        // Initialize the fragment manager.
        fm = getFragmentManager();

        // Initialize home screen fragments.
        history = fm.findFragmentByTag(TAG_FRAG_HISTORY);

        // Initialize start button
        selectMapButton = (Button) findViewById(R.id.goToSelectMap);

        selectMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RoutePlannerActivity.class);
                startActivity(intent);
            }
        });

        // Populate history fragment on view.
        if (history == null) {
            history = History.newInstance("a","z");
            fm.beginTransaction().replace(R.id.homeBottom, history, TAG_FRAG_HISTORY).commit();
            Toast.makeText(getApplicationContext(), "added history", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Simple method for creating a new toast.
    public void newToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
