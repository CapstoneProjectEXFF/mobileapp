package com.project.capstone.exchangesystem.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.project.capstone.exchangesystem.fragment.MyInventoryFragment;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.fragment.YourInventoryFragment;

public class TradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
    }


    public void toYourInventoryFragment(View view) {
        Fragment selectedFragment = null;
        selectedFragment = YourInventoryFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_switch, selectedFragment);
        transaction.commit();
    }

    public void toMyInventoryFragment(View view) {
        Fragment selectedFragment = null;
        selectedFragment = MyInventoryFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_switch, selectedFragment);
        transaction.commit();
    }
}
