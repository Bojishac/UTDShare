package com.example.utdshare;

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

    public void signOut(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        ParseUser.logOut();
    }

    public void createPost(View view) throws InterruptedException {
        EditText post = (EditText) findViewById(R.id.createPostEditText);
        if(post.getText().equals("")){
            Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
        }else{
            ParseObject object = new ParseObject("Post");
            object.put("Content", post.getText().toString());
            object.put("Username", ParseUser.getCurrentUser().getUsername());
            ArrayList<String> nullList = new ArrayList<>();
            object.add("LikedUsers", nullList);

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
        TimeUnit.SECONDS.sleep(1);
        loadFeed(null);

    }

    public void loadFeed(View view){
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
                    postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView <? > arg0, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(), ViewPostActivity.class);
                            intent.putExtra("Post", objects.get(objects.size()-1-(int) id));
                            startActivity(intent);
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

        loadFeed(null);

    }
}