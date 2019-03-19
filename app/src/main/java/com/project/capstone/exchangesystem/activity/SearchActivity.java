package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.adapter.ListAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.presenter.PresenterLogicTimKiem;
import com.project.capstone.exchangesystem.view.ViewTimKiem;
import com.project.capstone.exchangesystem.databinding.ActivitySearchBinding;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements ViewTimKiem {
    ActivitySearchBinding activitySearchBinding;
    ListAdapter adapter;
    ItemAdapter searchAdapter;
    ArrayList<String> arrayList = new ArrayList<>();

    PresenterLogicTimKiem presenterLogicTimKiem;
    RecyclerView recyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        presenterLogicTimKiem = new PresenterLogicTimKiem(this);
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


        adapter = new ListAdapter(arrayList);
        activitySearchBinding.listView.setAdapter(adapter);
        activitySearchBinding.search.setActivated(true);
        activitySearchBinding.search.setQueryHint("Type your keyword here");
        activitySearchBinding.search.onActionViewExpanded();
        activitySearchBinding.search.setIconified(false);
        activitySearchBinding.search.clearFocus();


        activitySearchBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenterLogicTimKiem.TimKiemSanPhamTheoTenSP(query, 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    public void TimKiemThanhCong(ArrayList<Item> sanPhamList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        activitySearchBinding.searchRecyclerView.setLayoutManager(layoutManager);
        activitySearchBinding.searchRecyclerView.setHasFixedSize(true);
        ItemAdapter resultAdapter = new ItemAdapter(this, sanPhamList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(getApplicationContext(), DescriptionItemActivity.class);
                intent.putExtra("descriptionItem", item);
                startActivity(intent);
            }
        });
        activitySearchBinding.searchRecyclerView.setAdapter(resultAdapter);
        resultAdapter.setfilter(sanPhamList);
    }

    @Override
    public void TimKiemThatBai() {

    }
}
