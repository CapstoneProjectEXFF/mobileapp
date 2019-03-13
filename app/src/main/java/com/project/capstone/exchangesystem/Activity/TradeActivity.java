package com.project.capstone.exchangesystem.Activity;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.project.capstone.exchangesystem.fragment.MyInventoryFragment;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.fragment.YourInventoryFragment;

public class TradeActivity extends AppCompatActivity {

    Fragment selectedFragment;
    Fragment defaultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        defaultFragment = YourInventoryFragment.newInstance();

        tx.replace(R.id.fragment_switch, defaultFragment);
        tx.commit();
    }


    public void toYourInventoryFragment(View view) {
        Fragment selectedFragment = null;
        selectedFragment = new YourInventoryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_switch, selectedFragment).addToBackStack(null);
        transaction.commit();

    }

    public void toMyInventoryFragment(View view) {
        Fragment selectedFragment = null;
        selectedFragment = new MyInventoryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_switch, selectedFragment).addToBackStack(null);
        transaction.commit();
    }

    public void sendTradeRequest(View view) {

    }


}
