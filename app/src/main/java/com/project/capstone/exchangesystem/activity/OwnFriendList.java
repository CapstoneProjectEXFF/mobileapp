package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.FriendListAdapter;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OwnFriendList extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    FriendListAdapter friendListAdapter;
    ArrayList<User> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_friend_list);

        direct();
        getData();
        actionToolbar();
    }

    private void direct() {
        toolbar = findViewById(R.id.friendToolbar);
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        listView = (ListView) findViewById(R.id.friendListview);
        friendList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(this, friendList);
        listView.setAdapter(friendListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getFriendListByUserId(authorization).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (response.isSuccessful()) {
                    List<User> temp = new ArrayList<>();
                    temp = response.body();
                    friendList.addAll(temp);
                    friendListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Fail rồi");
                Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actionToolbar() {
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
