package com.project.capstone.exchangesystem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.TradeAdapter;
import com.project.capstone.exchangesystem.model.Item;

import java.util.ArrayList;


public class YourInventoryFragment extends Fragment {
    GridView gridViewYourInventory;
    ArrayList<Item> yourInventoryList;
    TradeAdapter tradeAdapter;

    public YourInventoryFragment() {
    }


    public static YourInventoryFragment newInstance() {
        YourInventoryFragment fragment = new YourInventoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_inventory, container, false);
        gridViewYourInventory = (GridView) view.findViewById(R.id.gridViewYourInventory);
        yourInventoryList = new ArrayList<>();
        tradeAdapter = new TradeAdapter(view.getContext(), yourInventoryList);
        gridViewYourInventory.setAdapter(tradeAdapter);
        GetYourTradeInventory();
//        CheckItem();
        return view;
    }

    private void GetYourTradeInventory() {
        for (int i = 0; i < 4; i++) {
            yourInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
            yourInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
            yourInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
            yourInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
            tradeAdapter.notifyDataSetChanged();
        }
    }

//    private void CheckItem() {
//        int count = gridViewYourInventory.getAdapter().getCount();
//
//        for (int i = 0; i < count; i++) {
//
//            LinearLayout itemLayout = (LinearLayout) gridViewYourInventory.getChildAt(i); // Find by under LinearLayout
//            CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxTrade);
//            checkbox.setChecked(true);
//        }
//    }
}
