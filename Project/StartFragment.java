package com.example.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.databinding.FragmentStartBinding;

public class StartFragment extends Fragment {

    private FragmentStartBinding binding;


    public StartFragment() {
        // Required empty public constructor.
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // OnClickListener for the login button.
        binding.loginButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(StartFragmentDirections.actionStartFragmentToLoginFragment());
        });

        // OnClickListener for the register button.
        binding.registerButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(StartFragmentDirections.actionStartFragmentToRegisterFragment());
        });
    }
}