package com.project.capstone.exchangesystem.activity;

//import com.project.capstone.exchangesystem.adapter.ItemAdapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.fragment.NotificationFragment;
import com.project.capstone.exchangesystem.fragment.UserProfileFragment;
import com.project.capstone.exchangesystem.fragment.MainCharityPostFragment;
import com.project.capstone.exchangesystem.fragment.MainItemShowFragment;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class MainActivity extends AppCompatActivity {
    private final Fragment ITEM_FRAGMENT = MainItemShowFragment.newInstance();
    private final Fragment DONATION_FRAGMENT = MainCharityPostFragment.newInstance();
    //    private final Fragment NOTIFICATION_FRAGMENT = ;
    private final Fragment PROFILE_FRAGMENT = UserProfileFragment.newInstance();
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFirstFragment();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.bottombaritem_main:
                        selectedFragment = ITEM_FRAGMENT;
                        break;
                    case R.id.bottombaritem_charity:
                        selectedFragment = DONATION_FRAGMENT;
                        break;
                    case R.id.bottombaritem_notification:
                        selectedFragment = NotificationFragment.newInstance();
                        break;
                    case R.id.bottombaritem_profile:
                        selectedFragment = PROFILE_FRAGMENT;
                        break;
                }
                initFragment(selectedFragment);
                return true;
            }
        });
    }

    private void setFirstFragment() {
        String fragment = (String) getIntent().getSerializableExtra("fragment");

        if (fragment != null) {
            switch (fragment) {
                case DONATION_FRAGMENT_FLAG:
                    initFragment(DONATION_FRAGMENT);
                    break;
                case PROFILE_FRAGMENT_FLAG:
                    initFragment(PROFILE_FRAGMENT);
                    break;
                case ITEM_FRAGMENT_FLAG:
                    initFragment(ITEM_FRAGMENT);
                    break;
            }
        } else {
            initFragment(ITEM_FRAGMENT);
        }
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
}