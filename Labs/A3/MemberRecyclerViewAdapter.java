package com.example.a3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.a3.databinding.FragmentMemberItemBinding;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Member}.
 */
public class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Member> mMembers;

    public MemberRecyclerViewAdapter(ArrayList<Member> members) {
        mMembers = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates the view-holder.
        FragmentMemberItemBinding binding = FragmentMemberItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Member member = mMembers.get(position); // Gets the members.
        holder.binding.memberName.setText(member.getNamn()); // Sets the name of the members.
        holder.binding.memberEmail.setText(member.getEpost()); // Set the email of the members.
        holder.binding.memberAnswered.setText(member.getSvarade()); // Set the answered of the members.
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final FragmentMemberItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentMemberItemBinding.bind(itemView);
        }
    }
}
