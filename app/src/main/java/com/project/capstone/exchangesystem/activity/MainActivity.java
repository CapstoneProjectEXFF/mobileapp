package com.project.capstone.exchangesystem.activity;

//import com.project.capstone.exchangesystem.adapter.ItemAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.dialog.LoginOptionDialog;
import com.project.capstone.exchangesystem.fragment.*;
import com.project.capstone.exchangesystem.utils.UserSession;

import static com.project.capstone.exchangesystem.constants.AppStatus.CANCEL_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.LOGIN_REMINDER;

public class MainActivity extends AppCompatActivity implements LoginOptionDialog.LoginOptionListener {
    private final Fragment ITEM_FRAGMENT = MainItemShowFragment.newInstance();
    private final Fragment DONATION_FRAGMENT = MainCharityPostFragment.newInstance();
    private final Fragment NOTIFICATION_FRAGMENT = NotificationFragment.newInstance();
    private final Fragment PROFILE_FRAGMENT = UserProfileFragment.newInstance();
    private final Fragment MESSENGER_FRAGMENT = MessengerRoomFragment.newInstance();
    private final Fragment ADDFRIEND_FRAGMENT = AddFriendFragment.newInstance();
    private BottomNavigationView bottomNavigationView;
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSession = new UserSession(getApplicationContext());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = ITEM_FRAGMENT;
                switch (item.getItemId()) {
                    case R.id.bottombaritem_main:
                        selectedFragment = ITEM_FRAGMENT;
                        break;
                    case R.id.bottombaritem_charity:
                        selectedFragment = DONATION_FRAGMENT;
                        break;
                    case R.id.bottombaritem_notification:
                        if (userSession.isUserLoggedIn()) {
                            selectedFragment = NOTIFICATION_FRAGMENT;
                        } else {
                            selectedFragment = PROFILE_FRAGMENT;
                        }
                        break;
                    case R.id.bottombaritem_profile:
                        if (userSession.isUserLoggedIn()) {
                            selectedFragment = PROFILE_FRAGMENT;
                        } else {
                            selectedFragment = PROFILE_FRAGMENT;
                        }
                        break;
                    case R.id.bottombaritem_message:
                        if (userSession.isUserLoggedIn()) {
                            selectedFragment = MESSENGER_FRAGMENT;
                        } else {
                            selectedFragment = PROFILE_FRAGMENT;
                        }
                        break;
                }

                initFragment(selectedFragment);
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.bottombaritem_main);
    }

    private void initFragment(Fragment selectedFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }


    public void toSearch(View view) {
        Intent iTimKiem = new Intent(this, SearchActivity.class);
        startActivity(iTimKiem);
    }

    public void toOwnInventory(View view) {
        Intent iOwnInventory = new Intent(this, OwnInventory.class);
        startActivity(iOwnInventory);
    }

    public void toOwnTransaction(View view) {
        Intent iOwnTransaction = new Intent(this, OwnTransaction.class);
        startActivity(iOwnTransaction);
    }

    public void toOwnFriendList(View view) {
        Intent iOwnFriendList = new Intent(this, OwnFriendList.class);
        startActivity(iOwnFriendList);
    }

    public void toOwnDonationPost(View view) {
        Intent iOwnFriendList = new Intent(this, OwnDonationPost.class);
        startActivity(iOwnFriendList);
    }

    public void toLoginReminder(View view) {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case LOGIN_REMINDER:
                login();
                break;
            case CANCEL_IMAGE_OPTION:
                break;
            default:
                break;
        }
    }

    private void login() {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
    }
}