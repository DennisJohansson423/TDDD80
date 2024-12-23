package com.example.app;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.databinding.FragmentCommentBinding;
import java.util.ArrayList;

public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Comment> mComments;


    public MyCommentRecyclerViewAdapter(ArrayList<Comment> comments) {
        mComments = comments;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.mComment = comment;
        holder.mCommentID.setText(String.valueOf(comment.getUser_id()));
        holder.mCommentView.setText(comment.getComment());
        holder.mUsername.setText(holder.mComment.getUsername());
    }


    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mCommentID;
        public final TextView mCommentView;
        public final TextView mUsername;
        public Comment mComment;

        public ViewHolder(FragmentCommentBinding binding) {
            super(binding.getRoot());
            mCommentID = binding.postId;
            mCommentView = binding.postContent;
            mUsername = binding.username;
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mCommentView.getText() + "'";
        }
    }
}