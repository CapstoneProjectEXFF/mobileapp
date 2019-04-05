package com.project.capstone.exchangesystem.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.FriendFeedAdapter;
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
    String temp;
    Toolbar toolbar;

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
        toolbar = view.findViewById(R.id.exploreToolbar);
        friendFeedAdapter = new FriendFeedAdapter(view.getContext(), userList);
        listView.setAdapter(friendFeedAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });
        getData();
        ActionToolbar();
        return view;
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.explore_title);
    }

    private void getData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        final String authorization = sharedPreferences.getString("authorization", null);
        final int userID = sharedPreferences.getInt("userId", 0);

        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getNewFriendToAdd(authorization).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> temp = new ArrayList<>();
                    temp = response.body();
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getId() != userID) {
                            userList.add(temp.get(i));
                            friendFeedAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });
    }
}
