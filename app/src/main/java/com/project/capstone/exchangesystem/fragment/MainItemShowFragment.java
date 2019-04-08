package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.DescriptionItemActivity;
import com.project.capstone.exchangesystem.activity.SearchActivity;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class MainItemShowFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ArrayList<Item> itemArrayList;
    ItemAdapter itemAdapter;
    Menu menu;
    EditText txtSearch;
    ImageButton btnSearch;


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
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        final View view = inflater.inflate(R.layout.fragment_main_item_show, container, false);
        txtSearch = view.findViewById(R.id.txtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = txtSearch.getText().toString();
                rmaAPIService.findItemsByNameAndCategoryWithPrivacy(authorization, keyword, 0).enqueue(new Callback<ArrayList<Item>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                        if (response.isSuccessful()) {
                            ArrayList<Item> result = response.body();
                            Intent intent = new Intent(getActivity(), SearchActivity.class);
                            intent.putExtra("resultList", result);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Item is found", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "No Item is found", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        mainRecyclerView = (RecyclerView) view.findViewById(R.id.mainRecyclerView);
        itemArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(view.getContext(), itemArrayList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(view.getContext(), item.getDescription(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(view.getContext(), DescriptionItemActivity.class);
                intent.putExtra("descriptionItem", item);
                startActivity(intent);
            }
        });
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        mainRecyclerView.setAdapter(itemAdapter);
        GetBrandNewItems();
        return view;
    }


    private void GetBrandNewItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);
        final int meID = sharedPreferences.getInt("userId", 0);
        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getAllItemsWithPrivacy(authorization).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (response.isSuccessful()) {
                        List<Item> result = response.body();
                        if (result != null) {
                            for (int i = 0; i < result.size(); i++) {
                                if (result.get(i).getUser().getId() != meID && result.get(i).getStatus().equals(AppStatus.ITEM_ENABLE)) {
                                    itemArrayList.add(result.get(i));
                                    itemAdapter.notifyDataSetChanged();
                                }
                            }
                        }
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

    public void toSearch(View view) {
        Intent iTimKiem = new Intent(getActivity(), SearchActivity.class);
        startActivity(iTimKiem);
    }


}
