package com.example.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.databinding.FragmentRegisterBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fragment of the register page for the app.
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;


    public RegisterFragment() {
        // Required empty public constructor.
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerButtonUser.setOnClickListener(v -> {
            String username = binding.createUsername.getText().toString();
            String password = binding.createPassword.getText().toString();
            registerUser(username, password);
        });
    }


    private void registerUser(String username, String password) {
        String url = "http://127.0.0.1:5050/register";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Create JSON payload.
        JSONObject payload = new JSONObject();
        try {
            payload.put("username", username);
            payload.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        RegistrationResponse result = gson.fromJson(response.toString(), RegistrationResponse.class);

                        if (result.isSuccess()) {
                            Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            // Navigate to LoginFragment.
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigateUp();
                        } else {
                            Toast.makeText(requireContext(), "Registration failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}