package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.MyUsersRecyclerViewAdapter;
import com.example.app.User;
import com.example.app.databinding.FragmentUsersListBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private FragmentUsersListBinding binding;
    private MyUsersRecyclerViewAdapter adapter;
    private ArrayList<User> userList;
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";
    private RequestQueue queue;

    public UsersFragment() {
        // Required empty public constructor.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set up the RecyclerView
        RecyclerView recyclerView = binding.userRecyclerView;
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Initialize userList
        userList = new ArrayList<>();

        // Initialize the adapter with an empty list of users
        queue = Volley.newRequestQueue(requireActivity()); // Initialize the RequestQueue
        adapter = new MyUsersRecyclerViewAdapter(userList, requireContext(), queue);

        recyclerView.setAdapter(adapter);

        // Fetch the list of registered users
        fetchUsers();

        return view;
    }


    private void fetchUsers() {
        String url = "http://127.0.0.1:5050/get/allusers";
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        userList.clear();
                        adapter.notifyDataSetChanged();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                User user = gson.fromJson(jsonObject.toString(), User.class);
                                userList.add(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Log the response and userList
                        Log.d("Server Response", response.toString());
                        Log.d("User List", userList.toString());

                        updateUsers();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                            JSONArray jsonArray = jsonObject.getJSONArray("Following");
                            Gson gson = new Gson();

                            userList.clear();
                            adapter.notifyDataSetChanged();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject userObject = jsonArray.getJSONObject(i);
                                User user = gson.fromJson(userObject.toString(), User.class);
                                userList.add(user);
                            }

                            // Log the response and userList
                            Log.d("Error Response", jsonObject.toString());
                            Log.d("User List", userList.toString());

                            updateUsers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(request);
    }


    private void updateUsers() {
        RecyclerView recyclerView = binding.userRecyclerView;
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}