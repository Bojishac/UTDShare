package com.example.utdshare;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ViewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        try {
            loadPost(null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadPost(View view) throws ParseException {
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post");
        post.fetch();

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(post.getString("Username"));

        TextView contentTextView = findViewById(R.id.postContentTextView);
        contentTextView.setText((CharSequence) post.get("Content"));

        TextView likesTextView = findViewById(R.id.likesTextView);
        likesTextView.setText(post.getInt("Likes") + " Likes");

        Button likeButton = findViewById(R.id.likeButton);
        likeButton.setVisibility(View.VISIBLE);

        ArrayList likedUsers = (ArrayList) post.get("LikedUsers");
        if(likedUsers.contains(ParseUser.getCurrentUser().getUsername())){
            likeButton.setText("Unlike");
        }else{
            likeButton.setText("Like");
        }

        List<String> comments;
        final ListView postListView = (ListView) findViewById(R.id.commentListView);
        comments = post.getList("Comments");
        if(comments!=null){
            if(comments.size()>0){
                if(comments.size()>1){
                    String temp;
                    for(int i=0; i< comments.size()/2; i++){
                        temp = comments.get(i);
                        comments.set(i, comments.get(comments.size()-1-i));
                        comments.set(comments.size()-1-i, temp);
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, comments);
                postListView.setAdapter(arrayAdapter);
            }
        }



    }

    public void goBack(View view){
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    public void likePost(View view) throws InterruptedException, ParseException {
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post");
        post.fetch();
        Button likeButton = findViewById(R.id.likeButton);

        ArrayList<String> likedUsers = (ArrayList) post.get("LikedUsers");
        if(likedUsers.contains(ParseUser.getCurrentUser().getUsername())){
            Integer currentLikes = post.getInt("Likes");
            post.put("Likes", currentLikes-1);
            likedUsers.remove(ParseUser.getCurrentUser().getUsername());
            post.put("LikedUsers", likedUsers);
            post.saveInBackground();
            TextView likesTextView = findViewById(R.id.likesTextView);
            likesTextView.setText(post.getInt("Likes") + " Likes");
            likeButton.setText("Like");
        }else{
            Integer currentLikes = post.getInt("Likes");
            post.put("Likes", currentLikes+1);
            likedUsers.add(ParseUser.getCurrentUser().getUsername());
            post.put("LikedUsers", likedUsers);
            post.saveInBackground();
            TextView likesTextView = findViewById(R.id.likesTextView);
            likesTextView.setText(post.getInt("Likes") + " Likes");
            likeButton.setText("Unlike");
        }
    }

    public void postComment(View view) throws ParseException, InterruptedException {
        EditText commentText = findViewById(R.id.commentEditText);
        if(commentText.getText().toString().length()>250){
            Toast.makeText(this, "Comments are limited to 250 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if(commentText.getText().toString().equals("")){
            Toast.makeText(this, "Cannot make an empty comment", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post");
        post.fetch();
        List<String> comments = post.getList("Comments");
        if(comments==null){
            comments = new ArrayList<String>();
        }
        comments.add(commentText.getText().toString());
        commentText.setText("");
        post.put("Comments", comments);
        post.saveInBackground();
        Toast.makeText(ViewPostActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
        TimeUnit.SECONDS.sleep(1);
        loadPost(null);
    }


}