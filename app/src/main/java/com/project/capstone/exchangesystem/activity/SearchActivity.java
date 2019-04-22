package com.project.capstone.exchangesystem.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.adapter.ListAdapter;
import com.project.capstone.exchangesystem.databinding.ActivitySearchBinding;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding activitySearchBinding;
    ListAdapter adapter;
    Spinner spCategory;

    ItemAdapter resultAdapter;
    List<String> categoryList;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<Item> resultList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
//        ArrayList<Item> resultSearch = new ArrayList<>();
//        arrayList.add("iphone");
//        arrayList.add("sách");
//        arrayList.add("sách văn");
//        arrayList.add("iphone");
//        arrayList.add("bàn");
//        arrayList.add("bút");
//        arrayList.add("vở");
//        arrayList.add("cặp");
//        arrayList.add("áo quần");
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Category> result = response.body();
                        categoryList = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            categoryList.add(result.get(i).getName());
                        }
                        setDataForSpinner(activitySearchBinding.spCategory, categoryList);

                    } else {
                        notifyLoadingError("loadCategory", "httpstatus" + response.code());
                    }
                } else {
                    notifyLoadingError("loadCategory", "cannot load category");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                notifyLoadingError("loadCategory", "failed on calling API");
            }
        });
        Intent intent = this.getIntent();

        resultList = (ArrayList<Item>) intent.getSerializableExtra("resultList");
        adapter = new ListAdapter(arrayList);
//        activitySearchBinding.listView.setAdapter(adapter);
        activitySearchBinding.search.setActivated(true);
        activitySearchBinding.search.setQueryHint(getString(R.string.keyword_hint));
        activitySearchBinding.search.onActionViewExpanded();
        activitySearchBinding.search.setIconified(false);
        activitySearchBinding.search.clearFocus();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        activitySearchBinding.searchRecyclerView.setLayoutManager(layoutManager);
        activitySearchBinding.searchRecyclerView.setHasFixedSize(true);
        resultAdapter = new ItemAdapter(this, resultList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(getApplicationContext(), DescriptionItemActivity.class);
                intent.putExtra("descriptionItem", item);
                startActivity(intent);
            }
        });
        activitySearchBinding.searchRecyclerView.setAdapter(resultAdapter);
        activitySearchBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int categoryId = activitySearchBinding.spCategory.getSelectedItemPosition();
                RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
                rmaAPIService.findItemsByNameAndCategoryWithPrivacy(authorization, query, categoryId).enqueue(new Callback<ArrayList<Item>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {

                        if (response.isSuccessful()) {
                            ArrayList<Item> result = response.body();
                            resultList.removeAll(resultList);
                            resultList.addAll(result);
                            resultAdapter.setfilter(result);
                            resultAdapter.notifyDataSetChanged();
                        } else {
                            System.out.println("Null");
                            resultList.clear();
                            resultAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                        resultList.clear();
                        resultAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        this.getSupportActionBar().setNavigationOnClickListener


    }

    private void setDataForSpinner(Spinner spinner, List<String> dataArray) {
        List<String> spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Tất Cả");
        spinnerArrayList.addAll(dataArray);
        dataArray.add(0, "Tất Cả");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, spinnerArrayList);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
    }

    private void notifyLoadingError(String tag, String msg) {
        Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
        Log.i(tag, msg);
        Intent intent = new Intent(getApplicationContext(), OwnInventory.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
