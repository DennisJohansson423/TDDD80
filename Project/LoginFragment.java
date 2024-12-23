package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.Volley;
import com.example.app.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fragment of the login page for the app.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";
    private static final String USERNAME = "username";


    public LoginFragment() {
        // Required empty public constructor.
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButtonUser.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString();
            String password = binding.editPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(username, password);
            }
        });
    }


    private void loginUser(String username, String password) {
        String url = "http://127.0.0.1:5050/login";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

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
                        try {
                            String accessToken = response.getString("access_token");

                            // Save accessToken to SharedPreferences.
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(ACCESS_TOKEN_KEY, accessToken);
                            editor.putString(USERNAME, username);
                            editor.apply();

                            // Navigate to HomeFragment.
                            NavController navController = Navigation.findNavController(requireView());
                            NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeFragment();
                            navController.navigate(action);

                        } catch (JSONException e) {
                            e.printStackTrace();
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