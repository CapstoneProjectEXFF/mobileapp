package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TransactionConfirmActivity;
import com.project.capstone.exchangesystem.activity.TransactionDetailActivity;
import com.project.capstone.exchangesystem.adapter.FriendFeedAdapter;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class AddFriendFragment extends Fragment {
    ListView listView;
    FriendFeedAdapter friendFeedAdapter;
    ArrayList<User> userList;

    public AddFriendFragment() {
    }


    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        listView = (ListView) view.findViewById(R.id.friendFeedListview);
        userList = new ArrayList<>();
        friendFeedAdapter = new FriendFeedAdapter(view.getContext(), userList);
        listView.setAdapter(friendFeedAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });
        getData();
        return view;
    }


    private void getData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getAllUser(authorization).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> temp = response.body();
                    userList.addAll(temp);
                    friendFeedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });

    }


}
