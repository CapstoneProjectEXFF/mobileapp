package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.FriendListAdapter;
import com.project.capstone.exchangesystem.model.Relationship;
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
    ArrayList<Relationship> friendList;

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
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                User friendDetail = friendList.get(position);
//                Intent intent = new Intent(OwnFriendList.this, FriendInventoryActivity.class);
//                intent.putExtra("friendDetail", friendDetail);
//                startActivity(intent);
//            }
//        });
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        final int userID = sharedPreferences.getInt("userId", 0);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getFriendListByUserId(authorization).enqueue(new Callback<List<Relationship>>() {
            @Override
            public void onResponse(Call<List<Relationship>> call, Response<List<Relationship>> response) {

                if (response.isSuccessful()) {
                    List<Relationship> temp = new ArrayList<>();
                    temp = response.body();
                    friendList.addAll(temp);
                    friendListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Relationship>> call, Throwable t) {
                System.out.println("Fail rồi");
                Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actionToolbar() {
        toolbar.setTitle(getString(R.string.title_own_friend_list));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnSyncContact){
            Intent intent = new Intent(this, SyncContact.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.btnExploreFriend){
            Intent intent = new Intent(this, ExploreFriendActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
