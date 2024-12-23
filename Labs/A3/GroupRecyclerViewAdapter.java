package com.example.a3;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a3.databinding.FragmentGroupBinding;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Group}.
 */
public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mValues;

    public GroupRecyclerViewAdapter(ArrayList<String> groups) {
        mValues = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates the view-holder.
        return new ViewHolder(FragmentGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mGroup = mValues.get(position); // Gets the groups.
        holder.mGroupID.setText(String.valueOf(position + 1)); // Set the ids for the groups.
        holder.mGroupView.setText(holder.mGroup); // Set the groups names.
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mGroupID;
        public final TextView mGroupView;
        public String mGroup;

        public ViewHolder(@NonNull FragmentGroupBinding binding) {
            // Set the action that is called ones you select an group.
            super(binding.getRoot());
            mGroupID = binding.itemNumber;
            mGroupView = binding.group;
            mGroupView.setOnClickListener(view -> {
                GroupFragmentDirections.ActionGroupFragmentToMemberFragment action =
                        GroupFragmentDirections.actionGroupFragmentToMemberFragment(mGroup, "", "");
                NavController nav = Navigation.findNavController(mGroupView);
                nav.navigate(action);
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mGroupView.getText() + "'";
        }
    }
}