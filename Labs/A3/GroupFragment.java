package com.example.a3;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a3.databinding.FragmentGroupListBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A fragment representing a list of groups.
 */
public class GroupFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private FragmentGroupListBinding binding;
    private ArrayList<String> groupNames;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentGroupListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Context context = view.getContext();
        RecyclerView recyclerView = binding.groupList;

        // Set the adapter.
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        groupNames = new ArrayList<>();
        doVolleyGroup();
        return view;
    }

    private void doVolleyGroup() {
        // Gets the information about the groups from the given url
        // by using volley and gson to read and convert the information.
        String url = "https://tddd80-server-rmk.azurewebsites.net/grupper";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Group group = gson.fromJson(response, Group.class);

                        groupNames.clear();
                        groupNames.addAll(group.getGrupper());
                        binding.groupList.setAdapter(new GroupRecyclerViewAdapter(groupNames));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERROR", "Error in volley request" + error);
                    }
                });
        queue.add(request);
    }
}