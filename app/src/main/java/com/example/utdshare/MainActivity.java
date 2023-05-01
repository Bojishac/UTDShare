package com.example.utdshare;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public void showPostFeed(){     //called upon successful login, switch to post feed
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    public void logIn(View view){   //when the user presses login button
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText); //retrieve username and password
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null){     //note: login will only be successful if the user has verified their UTD email and the credentials match
                    Log.i("Signup", "Login successful");
                    showPostFeed(); //switch to post feed
                }else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();   //display error
                }
            }
        });
    }

    public void signUp(View view){  //called when user presses signup button
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText); //retrieve username and password
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){ //verify usernae and password exist
            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
        }else{
            ParseUser user = new ParseUser();   //create new user

            user.setUsername(usernameEditText.getText().toString());    //set username, password, and email
            user.setPassword(passwordEditText.getText().toString());
            user.setEmail(usernameEditText.getText().toString() + "@utdallas.edu"); //username must be netID, so the email will be valid and can be verified
            //at this point, the email is sent to the user at their utd email to verify the account.  This must be done before logging in


            user.signUpInBackground(new SignUpCallback() {  //sign up user
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        Log.i("Signup", "Successful");
                        ParseUser.logOut(); //log out user, as they must verify their email before logging in
                        Toast.makeText(MainActivity.this, "Please verify your UTD email. Check your spam folder too!", Toast.LENGTH_LONG).show();   //prompt user to verify email

                    }else{
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();    //hide title bar
        actionBar.setDisplayShowTitleEnabled(false);
        try {
            Parse.enableLocalDatastore(this);
        }catch (Exception e){
            e.printStackTrace();
        }


        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())   //create server connection using credentials
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        if (ParseUser.getCurrentUser()!=null){  //if user is already logged in upon opening app, display post feed
            showPostFeed();
        }

    }
}