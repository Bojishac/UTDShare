package com.example.utdshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    public void signOut(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        ParseUser.logOut();
    }

    public void createPost(View view){
        EditText post = (EditText) findViewById(R.id.createPostEditText);
        if(post.getText().equals("")){
            Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
        }else{
            ParseObject object = new ParseObject("Post");
            object.put("Content", post.getText().toString());
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Log.i("Parse Result", "Successful!");
                        Toast.makeText(FeedActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                        post.setText("");
                    } else{
                        Log.i("Parse Result", "Failed " + e.toString());
                    }
                }
            });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        final ArrayList<String> posts = new ArrayList<String>();
        final ListView postListView = (ListView) findViewById(R.id.postFeedListView);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, posts);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    Log.i("findInBackground", "Retrieved" + objects.size() + "objects");
                    if(objects.size() > 0){
                        for(int i = objects.size()-1; i>=0; i--){
                            posts.add(objects.get(i).getString("Content"));
                        }
                        postListView.setAdapter(arrayAdapter);
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });

    }
}