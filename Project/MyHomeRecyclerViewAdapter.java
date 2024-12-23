package com.example.app;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class MyHomeRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Post> mPosts;
    private final OnPostClickListener mListener;


    public MyHomeRecyclerViewAdapter(ArrayList<Post> posts, OnPostClickListener listener) {
        mPosts = posts;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPost = mPosts.get(position);
        holder.mPostID.setText(String.valueOf(position + 1));
        holder.mPostView.setText(holder.mPost.getMessage());
        holder.mUsername.setText(holder.mPost.getUsername());
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mPostID;
        public final TextView mPostView;
        public final TextView mUsername;
        public Post mPost;
        private final OnPostClickListener mListener;

        public ViewHolder(FragmentHomeBinding binding, OnPostClickListener listener) {
            super(binding.getRoot());
            mPostID = binding.postId;
            mPostView = binding.postContent;
            mUsername = binding.username;
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPostView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onPostClick(getAdapterPosition());
            }
        }
    }


    public interface OnPostClickListener {
        void onPostClick(int position);
    }
}