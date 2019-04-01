package com.project.capstone.exchangesystem.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.FriendFeedAdapter;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.ExffMessage;
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
        final String authorization = sharedPreferences.getString("authorization", null);
        final int userID = sharedPreferences.getInt("userId", 0);

        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getAllUser(authorization).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    final List<User> temp = response.body();
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getId() != userID && !temp.get(i).getStatus().equals(AppStatus.USER_DISABLE)) {
                            final int finalI = i;
                            rmaAPIService.checkRelationship(authorization, temp.get(i).getId()).enqueue(new Callback<ExffMessage>() {
                                @Override
                                public void onResponse(Call<ExffMessage> call, Response<ExffMessage> response) {
                                    if (response.isSuccessful()) {
                                        ExffMessage relationshipStatus = response.body();
                                        if (relationshipStatus.getMessage().equals("Not Friend")) {
                                            userList.add(temp.get(finalI));
                                            friendFeedAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ExffMessage> call, Throwable t) {

                                }
                            });
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
