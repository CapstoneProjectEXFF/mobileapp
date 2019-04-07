package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.nkzawa.socketio.client.Socket;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.fragment.MessengerTabFragment;
import com.project.capstone.exchangesystem.fragment.TradeTabFragment;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TradePagerAdapter extends FragmentStatePagerAdapter {

    public TradePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TradeTabFragment();
            case 1:
                return new MessengerTabFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        Context context = getApplicationContext();
//        switch (position) {
//            case 0:
//                return context.getString(R.string.trade);
//            case 1:
//                return context.getString(R.string.message);
//            default:
//                return null;
//        }
        return null;
    }
}
