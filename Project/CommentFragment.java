package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.databinding.FragmentCommentBinding;
import com.example.app.databinding.FragmentCommentListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment of the comment page for the app.
 */
public class CommentFragment extends Fragment {

    private FragmentCommentListBinding binding;
    private ArrayList<Comment> comments;
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";

    public CommentFragment() {
        // Required empty public constructor.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        RecyclerView recyclerView = binding.commentList;

        // Initialize the comments ArrayList before setting the adapter.
        comments = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new MyCommentRecyclerViewAdapter(comments));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CommentFragmentArgs args = CommentFragmentArgs.fromBundle(getArguments());
        String postId = args.getPostId(); // Get post ID from navigation arguments.

        // Fetch the like count for the given post from the server.
        fetchLikeCount(postId);

        binding.commentButton.setOnClickListener(v -> {
            String message = binding.newCommentMessage.getText().toString();

            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                postComment(message, postId);
            }
        });

        binding.likeButton.setOnClickListener(v -> {
            likePost(postId);
        });

        // Fetch the comments for the given post from the server.
        fetchComments(postId);
    }

    private void fetchComments(String postId) {
        String url = "http://127.0.0.1:5050/get/allcomments/" + postId;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        // Create a JSONObject with postId.
        JSONObject postData = new JSONObject();
        try {
            postData.put("messageId", postId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSON response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        ArrayList<Comment> commentArray = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Comment comment = gson.fromJson(jsonObject.toString(), Comment.class);
                                commentArray.add(comment);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateComments(commentArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR", "Error in volley request" + error);
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
        queue.add(request);
    }

    private void fetchLikeCount(String postId) {
        String url = "http://127.0.0.1:5050/get/likes/" + postId;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assume "likeCount" is the key for the like count in the response.
                            int likeCount = response.getInt("likes_count");
                            binding.likeCount.setText(String.valueOf(likeCount));  // Update the TextView.
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR", "Error in volley request" + error);
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
        queue.add(request);
    }

    private void updateComments(ArrayList<Comment> commentArray) {
        comments.clear();
        comments.addAll(commentArray);
        // Notify the adapter that the data has changed.
        RecyclerView recyclerView = binding.commentList;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void postComment(String message, String postId) {
        String url = "http://127.0.0.1:5050/post/comment";

        // Create the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve access token from SharedPreferences.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        // Get the username from SharedPreferences.
        String username = sharedPreferences.getString("username", "");

        // Create JSON payload.
        JSONObject payload = new JSONObject();
        try {
            payload.put("comment", message);
            payload.put("messageId", postId);
            payload.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSON response from the URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Comment posted by " + username + " successfully", Toast.LENGTH_SHORT).show();
                        binding.newCommentMessage.setText("");

                        // Navigate to HomeFragment after comment is posted.
                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(CommentFragmentDirections.actionCommentFragmentToHomeFragment());
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

    private void likePost(String postId) {
        String likeUrl = "http://127.0.0.1:5050/post/like";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Retrieve the current user from the access token.
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        // Get the user_id from SharedPreferences.
        int userId = sharedPreferences.getInt("user_id", 0);

        // Send the like request.
        sendLikeRequest(likeUrl, postId, accessToken, userId);
    }

    private void sendLikeRequest(String url, String postId, String accessToken, int userId) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // Create JSON payload.
        JSONObject payload = new JSONObject();
        try {
            payload.put("messageId", postId); // Change the key to "messageId"
            payload.put("userId", userId); // Include the userId in the payload
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Fetch the updated like count.
                        fetchLikeCount(postId);
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