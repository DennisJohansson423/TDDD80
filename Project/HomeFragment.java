package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.databinding.FragmentHomeListBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment of the home page for the app.
 */
public class HomeFragment extends Fragment implements MyHomeRecyclerViewAdapter.OnPostClickListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private FragmentHomeListBinding binding;
    private ArrayList<Post> posts;
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";
    private static final String USERNAME = "username";


    public HomeFragment() {
        // Required empty public constructor.
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    private void fetchFollowedMessages() {
        String url = "http://127.0.0.1:5050/get/followedmessages";
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String responseString = response.toString();
                        Log.d("Response", responseString);

                        Gson gson = new Gson();
                        ArrayList<Post> postArray = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Post post = gson.fromJson(jsonObject.toString(), Post.class);
                                postArray.add(post);
                            }

                            updatePosts(postArray);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR", "Error in fetchFollowedMessages request: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        queue.add(request);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentHomeListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        RecyclerView recyclerView = binding.postList;

        // Initialize the posts ArrayList before setting the adapter.
        posts = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new MyHomeRecyclerViewAdapter(posts, this));

        // Set the onClickListener for the new post button.
        Button newPostButton = binding.newPostButton;
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment());
            }
        });

        // Set the onClickListener for the logout button.
        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Set the onClickListener for the users button.
        Button usersButton = binding.usersButton;
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToUsersFragment());
            }
        });

        // Fetch the followed messages from the server
        fetchFollowedMessages();

        return view;
    }


    private void updatePosts(ArrayList<Post> postArray) {
        posts.clear();
        posts.addAll(postArray);
        // Notify the adapter that the data has changed.
        RecyclerView recyclerView = binding.postList;
        recyclerView.getAdapter().notifyDataSetChanged();
        Log.d("updatePosts", "Posts: " + posts);
    }


    private void logoutUser() {
        String url = "http://127.0.0.1:5050/logout";
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Clear the access token from SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(ACCESS_TOKEN_KEY);
                        editor.apply();

                        // Navigate back to the login screen
                        Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToStartFragment());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR", "Error in logout request" + error);
                    }
                }) {
            // Override the getHeaders method to set the access token as a header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        queue.add(request);
    }


    @Override
    public void onPostClick(int position) {
        // Get the ID of the clicked post.
        String postId = String.valueOf(posts.get(position).getId());
        String username = USERNAME;

        // Navigate to the CommentFragment, passing the post ID as an argument.
        HomeFragmentDirections.ActionHomeFragmentToCommentFragment action =
                HomeFragmentDirections.actionHomeFragmentToCommentFragment(postId, username);
        Navigation.findNavController(requireView()).navigate(action);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}