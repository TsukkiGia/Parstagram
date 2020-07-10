package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.fragments.ComposeFragment;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {


    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDescription;
        private ImageView ivImage;
        private TextView tvUsername;
        private TextView tvTimeCreated;
        private ImageView ivProfileImage;
        private ImageView ivLike;
        private ImageView ivComment;
        private TextView tvLikes;
        int likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimeCreated = itemView.findViewById(R.id.tvTimeCreated);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivComment = itemView.findViewById(R.id.ivComment);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivImage.setOnClickListener(this);
            ivProfileImage.setOnClickListener(this);
            ivLike.setOnClickListener(this);
            ivComment.setOnClickListener(this);
            ivLike.setTag(R.drawable.ufi_heart);
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

        public void bind(Post post) {
            Boolean didILike=false;
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
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
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
                Glide.with(context).load(post.getUser().getParseFile("ProfileImage").getUrl()).circleCrop().into(ivProfileImage);
            }
            if (didILike){
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                ivLike.setTag(R.drawable.ufi_heart_active);
                ivLike.setColorFilter(Color.RED);
            }
            else {
                ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart));
                ivLike.setTag(R.drawable.ufi_heart);
                ivLike.setColorFilter(Color.BLACK);
            }
            if (likes!=1) {
                tvLikes.setText(String.valueOf(likes) + " likes");
            }
            else {
                tvLikes.setText("1 like");
            }
            tvTimeCreated.setText(getRelativeTimeAgo(post.getTime().toString()));
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId()==ivImage.getId() || view.getId()==ivComment.getId()) {
                Post post = posts.get(position);
                Intent i = new Intent(context, PostDetails.class);
                i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(i);
            }
            if (view.getId()==ivProfileImage.getId()) {
                ParseUser user = posts.get(position).getUser();
                Intent i = new Intent(context, ProfileDetails.class);
                i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                i.putExtra("like",(Integer)ivLike.getTag());
                context.startActivity(i);
            }
            if (view.getId() == ivLike.getId()){
                if ((Integer) ivLike.getTag()==R.drawable.ufi_heart) {
                    ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                    ivLike.setTag(R.drawable.ufi_heart_active);
                    ivLike.setColorFilter(Color.RED);
                    likes++;
                    if (likes!=1) {
                        tvLikes.setText(String.valueOf(likes) + " likes");
                    }
                    else {
                        tvLikes.setText("1 like");
                    }
                    Post post = posts.get(getAdapterPosition());
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
                    ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ufi_heart));
                    ivLike.setTag(R.drawable.ufi_heart);
                    ivLike.setColorFilter(Color.BLACK);
                    likes--;
                    if (likes!=1) {
                        tvLikes.setText(String.valueOf(likes) + " likes");
                    }
                    else {
                        tvLikes.setText("1 like");
                    }
                    Post post = posts.get(getAdapterPosition());
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
        }
    }
}
