package com.example.a2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a2.databinding.FragmentInfoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    FragmentInfoBinding binding;
    InfoFragmentArgs args;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InfoFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        args = InfoFragmentArgs.fromBundle(getArguments());
        binding.info.setText(args.getInfo()); // Set the text for the title.
        binding.information.setText(args.getInformation()); // Set the text for the first information.
        binding.moreInformation.setText(args.getMoreInformation()); // Set the text for the second information.
        return binding.getRoot();
    }
}