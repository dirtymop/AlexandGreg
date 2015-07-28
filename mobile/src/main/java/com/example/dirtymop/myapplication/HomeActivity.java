package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class HomeActivity extends Activity {

    private static final String TAG_FRAG_HISTORY = "history_fragment";
    private static final String TAG_FRAG_START = "start_fragment";

    private FragmentManager fm;

    private Fragment history, start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the fragment manager.
        fm = getFragmentManager();

        // Initialize home screen fragments.
        history = fm.findFragmentByTag(TAG_FRAG_HISTORY);
        start = fm.findFragmentByTag(TAG_FRAG_START);

        // Populate fragments on view.
        if (history == null) {
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
}
