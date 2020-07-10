package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

public class SquarePostsAdapter extends RecyclerView.Adapter<SquarePostsAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    public SquarePostsAdapter(Context context, List<Post> posts) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_square_post,parent,false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivSquarePost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivSquarePost = itemView.findViewById(R.id.ivSquarePost);
        }

        public void bind(Post post) {
            Glide.with(context).load(post.getImage().getUrl()).centerCrop().into(ivSquarePost);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Post post = posts.get(position);
            Intent i = new Intent(context, PostDetails.class);
            i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
            context.startActivity(i);
        }
    }
}
