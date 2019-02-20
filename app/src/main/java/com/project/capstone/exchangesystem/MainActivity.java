package com.project.capstone.exchangesystem;

import Utils.RmaAPIUtils;
//import adapter.ItemAdapter;
import adapter.ItemAdapter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import model.Item;
import remote.RmaAPIService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
                        // TODO
                        return true;
                    case R.id.bottombaritem_notification:
                        // TODO
                        return true;
                    case R.id.bottombaritem_profile:
                        // TODO
                        return true;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
    }


}
