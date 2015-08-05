package com.example.dirtymop.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.dirtymop.myapplication.interfaces.HistoryInteractionListener;
import com.example.dirtymop.myapplication.interfaces.HomeInteractionListener;
import com.example.dirtymop.myapplication.interfaces.MenuInteractionListener;


// TODO: Need to implement the InteractionListener methods AFTER creating them in the interface files.
public class MainActivity extends Activity {
    public static final String PREFS_NAME = "firstName";
    // Fragment objects.
    private Fragment history, home, menu;

    // Fragment manager object.
    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String name = settings.getString("firstName", "");
        String id =settings.getString("iD","");


                TextView welcome= (TextView) findViewById(R.id.WelcomeMessage);
        welcome.setText("Welcome:"+name);
        // Dummy button to start home activity.
        Button g = (Button) findViewById(R.id.settingButton);
        Button b = (Button) findViewById(R.id.homeButton);
        Button h = (Button) findViewById(R.id.hudButton);



        g.setOnClickListener (new View.OnClickListener(){
            @Override public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HudActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
