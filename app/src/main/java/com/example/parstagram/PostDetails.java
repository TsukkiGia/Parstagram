package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    Post post;
    TextView tvDescription;
    TextView tvUsername;
    ImageView ivProfile;
    EditText etComment;
    public static final String TAG = "PostDetails";
    CommentsAdapter adapter;
    List<Comment> comments;
    RecyclerView rvComments;
    Button btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        comments = new ArrayList<>();
        tvDescription = findViewById(R.id.tvDescription);
        rvComments = findViewById(R.id.rvComments);
        ivProfile = findViewById(R.id.ivProfileImage);
        tvUsername = findViewById(R.id.tvUsername);
        btnComment = findViewById(R.id.btnComment);
        adapter = new CommentsAdapter(comments, PostDetails.this);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        tvUsername.setText(post.getUser().getUsername());
        etComment = findViewById(R.id.etComment);
        tvDescription.setText(post.getDescription());
        ParseFile image = post.getImage();
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setPost(post);
                comment.setUser(ParseUser.getCurrentUser());
                comment.setText(etComment.getText().toString());
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e!=null) {
                            Log.e(TAG,"Error while saving",e);
                            Toast.makeText(PostDetails.this, "Error while saving",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG,"Save successful");
                        etComment.setText("");
                    }
                });

            }
        });
        Glide.with(PostDetails.this).load(post.getUser().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfile);
        queryComments();
    }
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "Just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "A minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "An hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "Yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i("TAG", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }
    protected void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                for (Comment comment : comments) {
                    Log.i(TAG,"Description: "+comment.getText()+", User: "+comment.getUser().getUsername());
                }
                adapter.clear();
                adapter.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}