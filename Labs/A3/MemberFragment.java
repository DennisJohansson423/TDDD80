package com.example.a3;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.a3.databinding.FragmentMemberBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MemberFragment extends Fragment {

    FragmentMemberBinding binding;
    MemberFragmentArgs args;
    private RecyclerView mRecyclerView;
    private MemberRecyclerViewAdapter mAdapter;
    private ArrayList<Member> members;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        binding = FragmentMemberBinding.inflate(inflater, container, false);
        mRecyclerView = binding.memberList;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        args = MemberFragmentArgs.fromBundle(getArguments());
        fetchMembers(args.getName());
        return binding.getRoot();
    }

    public void fetchMembers(String groupName) {
        // Gets the information about the members from the given url
        // by using volley and gson to read and convert the information.
        String url = "https://tddd80-server-rmk.azurewebsites.net/medlemmar/" + groupName;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Member.GroupMembersResponse groupMembersResponse = gson.fromJson(response, Member.GroupMembersResponse.class);

                        members = new ArrayList<>(groupMembersResponse.getMedlemmar());
                        mAdapter = new MemberRecyclerViewAdapter(members);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", "Error in volley request" + error);
            }
        });
        queue.add(request);
    }
}