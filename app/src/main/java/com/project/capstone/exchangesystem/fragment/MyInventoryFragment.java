package com.project.capstone.exchangesystem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.MainCharityPostAdapter;
import com.project.capstone.exchangesystem.adapter.TradeAdapter;
import com.project.capstone.exchangesystem.model.CharityPostItem;
import com.project.capstone.exchangesystem.model.Item;

import java.util.ArrayList;


public class MyInventoryFragment extends Fragment {
    private GridView gridViewMyInventory;
    private ArrayList<Item> myInventoryList;
    private TradeAdapter tradeAdapter;


    public MyInventoryFragment() {
    }

    public static MyInventoryFragment newInstance() {
        MyInventoryFragment fragment = new MyInventoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_inventory, container, false);
        gridViewMyInventory = (GridView) view.findViewById(R.id.gridViewMyInventory);
        myInventoryList = new ArrayList<>();
        tradeAdapter = new TradeAdapter(view.getContext(), myInventoryList);
        gridViewMyInventory.setAdapter(tradeAdapter);
        GetMyTradeInventory();
        return view;
    }

    private void GetMyTradeInventory() {
        for (int i = 0; i < 3; i++) {
            myInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
        }
        tradeAdapter.notifyDataSetChanged();
    }
}
