package com.project.capstone.exchangesystem.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.FriendFeedAdapter;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncContact extends AppCompatActivity {
    ListView friendFeedListview;
    ArrayList<String> StoreContacts, phonenumberList;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    int userID;
    public static final int RequestPermissionCode = 1;
    Map<String, String> contactList;
    FriendFeedAdapter friendFeedAdapter;
    ArrayList<User> notFriendList;
    RmaAPIService rmaAPIService;
    String authorization;
    String userPhoneNumber;
    SharedPreferences sharedPreferences;
    Button btnshowFriend;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_contact);
        direct();
        EnableRuntimePermission();
        GetContactsIntoArrayList();
        phonenumberList = formatPhoneNumber(StoreContacts);
        for (int i = 0; i < phonenumberList.size(); i++) {
            System.out.println("text " + phonenumberList.get(i));
        }
        getNotFriendUser();
        ActionToolbar();
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ArrayList<String> formatPhoneNumber(ArrayList<String> storeContacts) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < storeContacts.size(); i++) {
            temp.add(storeContacts.get(i).replace(") ", "").replace("(", "").replace("-", ""));
        }
        return temp;
    }

    private void direct() {
        toolbar = findViewById(R.id.syncContactToolbar);
        rmaAPIService = RmaAPIUtils.getAPIService();
        contactList = new HashMap<String, String>();

        phonenumberList = new ArrayList<>();
        StoreContacts = new ArrayList<String>();
        notFriendList = new ArrayList<>();
        friendFeedListview = findViewById(R.id.friendFeedListview);
        friendFeedAdapter = new FriendFeedAdapter(getApplicationContext(), notFriendList);
        friendFeedListview.setAdapter(friendFeedAdapter);
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userID = sharedPreferences.getInt("userId", 0);
        userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        friendFeedListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });
    }

    public void GetContactsIntoArrayList() {
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.put(name, phonenumber);
            StoreContacts.add(phonenumber);
        }
        cursor.close();
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                SyncContact.this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(SyncContact.this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SyncContact.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SyncContact.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SyncContact.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getNotFriendUser() {
        rmaAPIService.getNotFriendFromContact(authorization, phonenumberList).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> temp = response.body();

                    friendFeedAdapter.notifyDataSetChanged();
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getId() != userID) {
                            notFriendList.add(temp.get(i));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


    }
}
