package com.example.a2;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a2.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.a2.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates the view-holder.
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position); // Gets the items.
        holder.mIdView.setText(mValues.get(position).id); // Gets the items id.
        holder.mContentView.setText(mValues.get(position).content); // Gets the content of the items.
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public PlaceholderItem mItem;

        public ViewHolder(FragmentItemBinding binding) {
            // Set the action that is called ones you select an item.
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mContentView.setOnClickListener(view -> {
                mContentView.setBackgroundColor(Color.RED);
                ItemFragmentDirections.ActionItemFragmentToInfoFragment action =
                        ItemFragmentDirections.actionItemFragmentToInfoFragment(mItem.id, mItem.content, mItem.info);
                action.setInformation("Information about item number " + mItem.id);
                action.setInfo(mItem.info);
                action.setMoreInformation(mItem.more_info);
                NavController nav = Navigation.findNavController(mContentView);
                nav.navigate(action);
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}