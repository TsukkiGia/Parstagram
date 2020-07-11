package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    TextView tvUsername;
    ImageView ivProfileImage;
    public static final String TAG = "Profile of User";
    ImageView ivGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));
        setContentView(R.layout.activity_profile_details);
        rvSquarePosts = findViewById(R.id.rvSquarePosts);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUsername = findViewById(R.id.tvUsername);
        ivGoBack = findViewById(R.id.ivGoBack);
        ivGoBack.setColorFilter(Color.BLACK);
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(ProfileDetails.this).load(user.getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfileImage);
        tvUsername.setText(user.getUsername());
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