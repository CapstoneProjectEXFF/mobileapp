package com.project.capstone.exchangesystem.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.TradeAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class YourInventoryFragment extends Fragment {
    private GridView gridViewYourInventory;
    private ArrayList<Item> yourInventoryList;
    private TradeAdapter tradeAdapter;

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

        Item item = (Item) getActivity().getIntent().getSerializableExtra("descriptionItem");
        String idTradeItem = String.valueOf(item.getId());


        return view;
    }

    private void GetYourTradeInventory() {
        Item item = (Item) getActivity().getIntent().getSerializableExtra("descriptionItem");
        int yourID = item.getUser().getId();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getItemsByUserId(yourID).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    List<Item> result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        yourInventoryList.add(result.get(i));
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


}
