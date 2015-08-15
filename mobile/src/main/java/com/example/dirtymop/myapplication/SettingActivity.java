package com.example.dirtymop.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dirtymop.myapplication.R;

//import com.example.dirtymop.myapplication.fragments.StartMini;
import com.example.dirtymop.myapplication.fragments.settings;


public class SettingActivity
        extends ActionBarActivity
        implements settings.SettingsInteractionListener {


    private FragmentManager fm;
    private Fragment settings1;
    private static final String TAG_Settings = "settings_Tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Button Settings=(Button)findViewById(R.id.button6);
        //Button ActiveMoveMap = (Button) findViewById(R.id.button7);


        // Initialize the fragment manager.
        fm = getFragmentManager();

        // Initialize home screen fragments.
        settings1 = fm.findFragmentByTag(TAG_Settings);





        if (settings1 == null) {
            settings1 = settings.newInstance("a", "z");
            fm.beginTransaction().replace(R.id.frag, settings1, TAG_Settings).commit();
           // Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
        }
    }


    public void onFragmentInteraction(Uri uri)
    {}

    @Override
    public void processClearDatabase() {
        new AlertDialog.Builder(this)
                .setTitle("Clear history")
                .setMessage("Are you sure you want to delete all history entries?\nThis action cannot be undone.")
                .setPositiveButton("Yes please.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApplication().deleteDatabase("local.db");
                        Toast.makeText(getApplicationContext(), "History successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No way!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setting, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}

