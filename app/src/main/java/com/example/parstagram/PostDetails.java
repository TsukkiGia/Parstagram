package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    ImageView ivMedia;
    EditText etComment;
    public static final String TAG = "PostDetails";
    CommentsAdapter adapter;
    List<Comment> comments;
    RecyclerView rvComments;
    Button btnComment;
    ImageView ivLike;
    TextView tvLikes;
    TextView tvDateTime;
    ProgressBar pbLoading;
    ImageView ivGoBack;
    int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ivGoBack = findViewById(R.id.ivGoBack);
        ivGoBack.setColorFilter(Color.BLACK);
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivLike = findViewById(R.id.ivLike);
        tvLikes = findViewById(R.id.tvLikes);
        tvDateTime = findViewById(R.id.tvDateTime);
        pbLoading = findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        Boolean didILike=false;
        JSONArray likers = post.getLikes();
        for (int i = 0; i<likers.length();i++) {
            try {
                JSONObject user = (JSONObject) likers.get(i);
                String userID = user.getString("objectId");
                if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                    didILike=true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        likes = likers.length();
        if (likes!=1) {
            tvLikes.setText(String.valueOf(likes) + " likes");
        }
        else {
            tvLikes.setText("1 like");
        }
        if (didILike){
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart_active));
            ivLike.setTag(R.drawable.ufi_heart_active);
            ivLike.setColorFilter(Color.RED);
        }
        else {
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart));
            ivLike.setTag(R.drawable.ufi_heart);
            ivLike.setColorFilter(Color.BLACK);
        }
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Integer) ivLike.getTag()==R.drawable.ufi_heart) {
                    ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart_active));
                    ivLike.setTag(R.drawable.ufi_heart_active);
                    ivLike.setColorFilter(Color.RED);
                    likes++;
                    if (likes!=1) {
                        tvLikes.setText(String.valueOf(likes) + " likes");
                    }
                    else {
                        tvLikes.setText("1 like");
                    }
                    JSONArray likers = post.getLikes();
                    likers.put(ParseUser.getCurrentUser());
                    post.setLikes(likers);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            Log.i("try","try");
                        }
                    });
                }
                else {
                    ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ufi_heart));
                    ivLike.setTag(R.drawable.ufi_heart);
                    ivLike.setColorFilter(Color.BLACK);
                    likes--;
                    if (likes!=1) {
                        tvLikes.setText(String.valueOf(likes) + " likes");
                    }
                    else {
                        tvLikes.setText("1 like");
                    }
                    int index = 0;
                    JSONArray likers = post.getLikes();
                    for (int i = 0; i<likers.length()-1;i++) {
                        try {
                            ParseUser user = (ParseUser) likers.get(i);
                            if (user.equals(ParseUser.getCurrentUser())) {
                                index=i;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    likers.remove(index);
                    post.setLikes(likers);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            Log.i("try","try");
                        }
                    });
                }
            }
        });
        comments = new ArrayList<>();
        tvDateTime.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));
        tvDescription = findViewById(R.id.tvDescription);
        rvComments = findViewById(R.id.rvComments);
        ivProfile = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);
        tvUsername = findViewById(R.id.tvUsername);
        btnComment = findViewById(R.id.btnComment);
        adapter = new CommentsAdapter(comments, PostDetails.this);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        tvUsername.setText(post.getUser().getUsername());
        etComment = findViewById(R.id.etComment);
        tvDescription.setText(post.getDescription());
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Comment comment = new Comment();
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
                        comments.add(comments.size(),comment);
                        adapter.notifyItemInserted(comments.size()-1);
                        etComment.setText("");
                    }
                });

            }
        });
        Glide.with(PostDetails.this).load(post.getUser().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfile);
        Glide.with(PostDetails.this).load(post.getParseFile(Post.KEY_IMAGE).getUrl()).centerCrop().into(ivMedia);
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
        query.whereEqualTo(Comment.KEY_POST,post);
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
                pbLoading.setVisibility(View.INVISIBLE);
            }
        });
    }
}