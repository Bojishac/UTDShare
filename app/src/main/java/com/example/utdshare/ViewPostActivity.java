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
        ActionBar actionBar = getSupportActionBar();    //hide title bar
        actionBar.setDisplayShowTitleEnabled(false);
        try {
            loadPost(null);     //load and display post data
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadPost(View view) throws ParseException { //function to load and display post data, called on create, when user comments/likes, or when refresh button is pressed
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post");
        post.fetch();   //retrieve fresh data

        TextView usernameTextView = findViewById(R.id.usernameTextView);    //display username of the poster
        usernameTextView.setText(post.getString("Username"));

        TextView contentTextView = findViewById(R.id.postContentTextView);  //display post content
        contentTextView.setText((CharSequence) post.get("Content"));

        TextView likesTextView = findViewById(R.id.likesTextView);          //display number of likes
        likesTextView.setText(post.getInt("Likes") + " Likes");

        Button likeButton = findViewById(R.id.likeButton);
        likeButton.setVisibility(View.VISIBLE);

        ArrayList likedUsers = (ArrayList) post.get("LikedUsers");          //retrieve list of users who have liked the post
        if(likedUsers.contains(ParseUser.getCurrentUser().getUsername())){  //if current user has liked the post
            likeButton.setText("Unlike");                                   //set option to "Unlike"
        }else{                                                              //if current user has not liked the post
            likeButton.setText("Like");                                     //set option to "Like"
        }

        List<String> comments;  //initialize comment list
        final ListView commentListView = (ListView) findViewById(R.id.commentListView);    //initialize list view to display comments
        comments = post.getList("Comments");    //retrieve comments from post
        if(comments!=null){     //check to make sure comments exist before trying to display them
            if(comments.size()>0){
                if(comments.size()>1){
                    String temp;
                    for(int i=0; i< comments.size()/2; i++){    //loop to reverse order of comments so newest comments display first
                        temp = comments.get(i);
                        comments.set(i, comments.get(comments.size()-1-i));
                        comments.set(comments.size()-1-i, temp);
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, comments);  //initialize array adapter
                commentListView.setAdapter(arrayAdapter);   //display comment list
            }
        }



    }

    public void goBack(View view){  //switch back to post feed if "back" button is pressed
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    public void likePost(View view) throws InterruptedException, ParseException {   //function called when user likes or dislikes post
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post"); //retrieve fresh post data
        post.fetch();
        Button likeButton = findViewById(R.id.likeButton);

        ArrayList<String> likedUsers = (ArrayList) post.get("LikedUsers");  //get list of users who have liked the post
        if(likedUsers.contains(ParseUser.getCurrentUser().getUsername())){  //if user has already liked the post:
            Integer currentLikes = post.getInt("Likes");
            post.put("Likes", currentLikes-1);                              //decrement likes
            likedUsers.remove(ParseUser.getCurrentUser().getUsername());    //remove user from list of users who have liked the post
            post.put("LikedUsers", likedUsers);                             //save updated data
            post.saveInBackground();
            TextView likesTextView = findViewById(R.id.likesTextView);
            likesTextView.setText(post.getInt("Likes") + " Likes");     //update displayed likes
            likeButton.setText("Like");                                     //switch button from "Unlike" to "Like"
        }else{                                                              //if user has not already liked the post:
            Integer currentLikes = post.getInt("Likes");
            post.put("Likes", currentLikes+1);                              //increment likes
            likedUsers.add(ParseUser.getCurrentUser().getUsername());       //add user to list of users who have liked the post
            post.put("LikedUsers", likedUsers);
            post.saveInBackground();                                        //save updated data
            TextView likesTextView = findViewById(R.id.likesTextView);
            likesTextView.setText(post.getInt("Likes") + " Likes");     //display updated likes
            likeButton.setText("Unlike");                                   //switch option from "Like" to "Unlike"
        }
    }

    public void postComment(View view) throws ParseException, InterruptedException {
        EditText commentText = findViewById(R.id.commentEditText);
        if(commentText.getText().toString().length()>250){      //verify comment is <= 250 characters
            Toast.makeText(this, "Comments are limited to 250 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if(commentText.getText().toString().equals("")){        //verify comment has at least one character
            Toast.makeText(this, "Cannot make an empty comment", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle b = getIntent().getExtras();
        ParseObject post = b.getParcelable("Post");
        post.fetch();                                           //get updated post data
        List<String> comments = post.getList("Comments");
        if(comments==null){
            comments = new ArrayList<String>();
        }
        comments.add(commentText.getText().toString());         //add comment to list of comments
        commentText.setText("");                                //clear comment edit text
        post.put("Comments", comments);                         //push new list of comments to server
        post.saveInBackground();                                //save updated data
        Toast.makeText(ViewPostActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
        TimeUnit.SECONDS.sleep(1);                      //wait to verify updates go through before updated data is displayed
        loadPost(null);                                     //display updated post with new comments
    }


}