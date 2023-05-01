package com.example.utdshare;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FeedActivity extends AppCompatActivity {

    public void signOut(View view){ //called when sign out button is pressed, log out the user and switch to log in activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        ParseUser.logOut();
    }

    public void createPost(View view) throws InterruptedException { //create post, called when create post button is pressed
        EditText post = findViewById(R.id.createPostEditText);      //retrieve post that the user has entered
        if(post.getText().toString().equals("") || post.getText() == null){ //verify post is not empty
            Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
        }else if(post.getText().toString().length()>250){                   //verify post is under character limit
            Toast.makeText(this, "Posts are limited to 250 characters", Toast.LENGTH_SHORT).show();
        } else{
            ParseObject object = new ParseObject("Post");   //create new post
            object.put("Content", post.getText().toString());           //push post content
            object.put("Username", ParseUser.getCurrentUser().getUsername());   //set post username to current user
            ArrayList<String> nullList = new ArrayList<>();
            object.add("LikedUsers", nullList); //create liked users parameter

            object.saveInBackground(new SaveCallback() {    //save post to server
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Log.i("Parse Result", "Successful!");
                        Toast.makeText(FeedActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                        post.setText("");   //clear post edit text
                    } else{
                        Log.i("Parse Result", "Failed " + e.toString());
                    }
                }
            });
        }
        TimeUnit.SECONDS.sleep(1);  //sleep before updating feed to ensure accuracy
        loadFeed(null);

    }

    public void loadFeed(View view){    //update feed, called on create, when post is made, and when refresh button is pressed
        final ArrayList<String> posts = new ArrayList<String>();    //initialize variables to display posts
        final ListView postListView = (ListView) findViewById(R.id.postFeedListView);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, posts);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");    //fetch post objects

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    Log.i("findInBackground", "Retrieved" + objects.size() + "objects");
                    if(objects.size() > 0){
                        for(int i = objects.size()-1; i>=0; i--){   //add content to display from each post
                            posts.add(objects.get(i).getString("Content"));
                        }
                        postListView.setAdapter(arrayAdapter);  //display content
                    }
                    postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //wait for a post to be clicked
                        public void onItemClick(AdapterView <? > arg0, View view, int position, long id) {  //if a post is clicked
                            Intent intent = new Intent(getApplicationContext(), ViewPostActivity.class);
                            intent.putExtra("Post", objects.get(objects.size()-1-(int) id));    //pass the post to a new activity
                            startActivity(intent);  //start view post activity to display post data
                        }

                    });
                }else{
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ActionBar actionBar = getSupportActionBar();    //hide title bar
        actionBar.setDisplayShowTitleEnabled(false);

        loadFeed(null); //load post feed

    }
}