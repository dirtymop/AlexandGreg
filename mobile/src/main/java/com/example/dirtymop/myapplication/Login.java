package com.example.dirtymop.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class Login extends ActionBarActivity {
    public static final String PREFS_NAME = "firstName";
    Button checkuser;
    LoginButton loginButton;
    CallbackManager callbackManager;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("result", "onActivityREsult was called...");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        checkuser = (Button) findViewById(R.id.check);

        // Sets facebook permissions
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");

        // Ensures login and moves to Main Activity
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (accessToken != null) {
            if (profile != null) {

                String firstname = Profile.getCurrentProfile().getFirstName();
                String facebookidnumber= Profile.getCurrentProfile().getId();
                AccessToken x = AccessToken.getCurrentAccessToken();
                String y = x.toString();

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("firstName", firstname);
                editor.putString("iD", facebookidnumber);


                // Commit the edits!
                editor.commit();



                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
                }
        else
        {
            Log.d("tag6", "You are not logged in!");
        }

            // Outputs to log info about user
        checkuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("tag1", loginButton.getLoginBehavior().toString());


                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Profile profile = Profile.getCurrentProfile();


                //Attempts to recognize if the user has logged in before
                if (accessToken != null) {
                    if (profile != null) {

                        String b= Profile.getCurrentProfile().getFirstName();
                        AccessToken x = AccessToken.getCurrentAccessToken();
                        String y = x.toString();
                        Log.d("tag2",b + y);

                    }
                } else {
                    Log.d("tag2", "NUll user info");
                }
            }
        });




        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("tag1", "onSuccess");
                Intent intent = new Intent(Login.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancel() {
                // App code
                Log.d("tag1", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "bad",
                        Toast.LENGTH_LONG).show();
                Log.d("tag1", "onError");

            }
        });


//        loginButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.d("tag1", "onCLick");
//
//           //     loginButton.notify();
//
////                Intent intent = new Intent(MainActivity.this, FacebookActivity.class);
////                startActivity(intent);
//
//                callbackManager.onActivityResult();
//
//
//
//            }
//        });


    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
