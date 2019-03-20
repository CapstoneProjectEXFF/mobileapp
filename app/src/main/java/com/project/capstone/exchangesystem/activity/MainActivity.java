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

public class MainActivity extends AppCompatActivity {
    private final Fragment DEFAULT_FRAGMENT = MainItemShowFragment.newInstance();
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.bottombaritem_main:
                        selectedFragment = MainItemShowFragment.newInstance();
                        break;
                    case R.id.bottombaritem_charity:
                        selectedFragment = MainCharityPostFragment.newInstance();
                        break;
                    case R.id.bottombaritem_notification:
                        selectedFragment = NotificationFragment.newInstance();
                        break;
                    case R.id.bottombaritem_profile:
                        selectedFragment = UserProfileFragment.newInstance();
                        break;
                }
                initFragment(selectedFragment);
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.bottombaritem_main);
    }

    private void initFragment(Fragment selectedFragment){
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
