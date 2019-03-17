package com.project.capstone.exchangesystem.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.TradeAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


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
//        for (int i = 0; i < 3; i++) {
//            myInventoryList.add(new Item(1, "iphone 7 " + i, "417 Quang Trung", "1", "1", "2", null, null, null, null, null));
//        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        int userID = sharedPreferences.getInt("userId", 0);

        // Get a List of item ID
        ArrayList<String> itemList = getArrayList("itemMeIdList");


        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getItemsByUserId(userID).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    List<Item> result = response.body();
//                    myInventoryList.addAll(result);
                    for (int i = 0; i < result.size(); i++) {
                        myInventoryList.add(result.get(i));
                        tradeAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        } else {
            System.out.println("Fail Test Authorization");
        }
        tradeAdapter.notifyDataSetChanged();
    }


        public ArrayList<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> strings = gson.fromJson(json, type);
        return strings;
    }
}
