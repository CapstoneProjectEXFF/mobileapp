package com.project.capstone.exchangesystem;

import Utils.RmaAPIUtils;
import adapter.ItemAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewFlipper;
import model.Item;
import model.User;
import remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MainItemShowFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ArrayList<Item> itemArrayList;
    ItemAdapter itemAdapter;


    public MainItemShowFragment() {
    }

    public static MainItemShowFragment newInstance() {
        MainItemShowFragment fragment = new MainItemShowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_item_show, container, false);
        mainRecyclerView = (RecyclerView) view.findViewById(R.id.mainRecyclerView);
        itemArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(view.getContext(), itemArrayList);
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        mainRecyclerView.setAdapter(itemAdapter);
        GetBrandNewItems();
        return view;
    }

    private void GetBrandNewItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getAllItems(authorization).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    System.out.println("Done first step in Show Item");
                    List<Item> result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        itemArrayList.add(result.get(i));
                        itemAdapter.notifyDataSetChanged();
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
    }
}
