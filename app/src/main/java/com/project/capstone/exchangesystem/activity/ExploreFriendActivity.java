package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class ExploreFriendActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ListView listView;
    FriendFeedAdapter friendFeedAdapter;
    ArrayList<User> userList;
    String temp;
    Toolbar toolbar;
    String authorization;
    int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_friend);
        direct();
        getData();
        setToolbar();

    }

    private void direct() {
        listView = (ListView) findViewById(R.id.friendFeedListview);
        userList = new ArrayList<>();
        toolbar = findViewById(R.id.exploreToolbar);
        friendFeedAdapter = new FriendFeedAdapter(getApplicationContext(), userList);
        listView.setAdapter(friendFeedAdapter);
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
    }

    private void getData() {
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getNewFriendToAdd(authorization).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> temp = new ArrayList<>();
                    temp = response.body();
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getId() != userId) {
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

    private void setToolbar() {
        toolbar.setTitle(R.string.title_explore_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
