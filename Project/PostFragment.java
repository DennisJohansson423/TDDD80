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
import com.example.app.databinding.FragmentPostBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment of the post page for the app.
 */
public class PostFragment extends Fragment {

    private FragmentPostBinding binding;
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";


    public PostFragment() {
        // Required empty public constructor.
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the onClickListener for the post button.
        binding.postButton.setOnClickListener(v -> {
            String message = binding.newPostMessage.getText().toString();

            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in the message field", Toast.LENGTH_SHORT).show();
            } else {
                postMessage(message);
            }
        });
    }


    private void postMessage(String message) {
        String url = "http://127.0.0.1:5050/post/message";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        // Get the username from SharedPreferences.
        String username = sharedPreferences.getString("username", "");

        // Create JSON payload.
        JSONObject payload = new JSONObject();
        try {
            payload.put("message", message);
            payload.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Message posted by " + username + " successfully", Toast.LENGTH_SHORT).show();
                        binding.newPostMessage.setText("");

                        NavController navController = Navigation.findNavController(requireView());
                        NavDirections action = PostFragmentDirections.actionPostFragmentToHomeFragment();
                        navController.navigate(action);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            // Override the getHeaders method to set the access token as a header.
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}