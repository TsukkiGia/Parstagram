package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetails extends AppCompatActivity {
    RecyclerView rvSquarePosts;
    SquarePostsAdapter adapter;
    List<Post> posts;
    ParseUser user;
    public static final String TAG = "Profile of User";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Help","im here");
        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));
        setContentView(R.layout.activity_profile_details);
        rvSquarePosts = findViewById(R.id.rvSquarePosts);
        posts = new ArrayList<>();
        adapter = new SquarePostsAdapter(ProfileDetails.this,posts);
        rvSquarePosts.setLayoutManager(new GridLayoutManager(ProfileDetails.this,3));
        rvSquarePosts.setAdapter(adapter);
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER,user);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Post post : posts) {
                    Log.i(TAG,"Description: "+post.getDescription()+", User: "+post.getUser().getUsername());
                }
                adapter.clear();
                adapter.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}