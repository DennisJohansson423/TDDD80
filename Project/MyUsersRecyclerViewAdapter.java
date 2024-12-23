package com.example.app;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.app.databinding.FragmentUsersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<User> mValues;
    private final SharedPreferences sharedPreferences;
    private static final String PREFERENCES_FILE_NAME = "com.example.app_preferences";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private final RequestQueue queue;
    private final Context context;
    private List<String> followingUsernames = new ArrayList<>();


    public MyUsersRecyclerViewAdapter(ArrayList<User> items, Context context, RequestQueue queue) {
        mValues = items;
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        this.queue = queue;
        this.context = context;
        getFollowingUsernames();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentUsersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUsername.setText(holder.mItem.getUsername());

        String username = holder.mItem.getUsername();
        Log.d("Username", "username: " + username);

        if (username != null && !username.isEmpty()) {
            if (followingUsernames.contains(username)) {
                holder.mFollowButton.setText("Unfollow");
                Log.e("Follow worked", "Followed" + username);
            } else {
                holder.mFollowButton.setText("Follow");
                Log.e("No follow", "Not followed a person yet");
            }

            holder.mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (followingUsernames.contains(username)) {
                        unfollowUser(username, holder);
                        holder.mFollowButton.setText("Follow");
                    } else {
                        followUser(username, holder);
                        holder.mFollowButton.setText("Unfollow");
                    }
                }
            });
        } else {
            Log.e("Follow Error", "username is null");
        }
    }


    private void getFollowingUsernames() {
        String followingUrl = "http://127.0.0.1:5050/get/following";
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

        StringRequest request = new StringRequest(Request.Method.GET, followingUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray followingArray = jsonObject.getJSONArray("Following");
                            for (int i = 0; i < followingArray.length(); i++) {
                                followingUsernames.add(followingArray.getString(i));
                            }
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Get Following Error", "Error getting following users: " + error.getMessage());
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


    private void followUser(String username, ViewHolder holder) {
        Log.e("FollowUser", "Clicked follow on: " + username);
        if (username != null && !username.isEmpty()) {
            String followUrl = "http://127.0.0.1:5050/follow/" + username;
            String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");

            Log.d("AccessToken", "Access Token is: " + accessToken);
            Log.e("Username", "Username is: " + username);

            if (accessToken != null && !accessToken.isEmpty()) {
                StringRequest request = new StringRequest(Request.Method.POST, followUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                followingUsernames.add(username);
                                notifyDataSetChanged();
                                Log.e("Follow DONE", "Followed: " + username);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Follow Error", "Error following user: " + error.getMessage() + " " + username);
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
            } else {
                Log.e("Follow Error", "Access token is empty or null");
            }
        } else {
            Log.e("Follow Error", "Username is null here");
        }
    }


    private void unfollowUser(String username, ViewHolder holder) {
        if (username != null && !username.isEmpty()) {
            String unfollowUrl = "http://127.0.0.1:5050/unfollow/" + username;
            String accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, "");
            Log.d("AccessToken", "Access Token: " + accessToken);

            if (accessToken != null && !accessToken.isEmpty()) {
                StringRequest request = new StringRequest(Request.Method.DELETE, unfollowUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                followingUsernames.remove(username);
                                notifyDataSetChanged();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Unfollow Error", "Error unfollowing user: " + error.getMessage());
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
            } else {
                Log.e("Unfollow Error", "Access token is empty or null here...");
            }
        } else {
            Log.e("Unfollow Error", "name null here");
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUsername;
        public final Button mFollowButton;
        public User mItem;

        public ViewHolder(FragmentUsersBinding binding) {
            super(binding.getRoot());
            mUsername = binding.username;
            mFollowButton = binding.followButton;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsername.getText() + "'";
        }
    }
}