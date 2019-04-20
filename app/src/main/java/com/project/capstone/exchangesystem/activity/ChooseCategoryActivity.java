package com.project.capstone.exchangesystem.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.CategoryAdapter;
import com.project.capstone.exchangesystem.adapter.CategoryListAdapter;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseCategoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvCategory;
    ArrayList<Category> selectedCategoryList;
    RmaAPIService rmaAPIService;
    Context context;
    CategoryListAdapter categoryListAdapter;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        getComponents();
        setToolbar();
        setCategoryData();
    }

    private void setCategoryData() {
        selectedCategoryList = (ArrayList<Category>) getIntent().getSerializableExtra("selectedCategory");

        categoryListAdapter = new CategoryListAdapter(context, selectedCategoryList);
        lvCategory.setAdapter(categoryListAdapter);
    }

    private void getComponents() {
        context = this;
        toolbar = findViewById(R.id.tbToolbar);
        lvCategory = findViewById(R.id.lvCategory);
        selectedCategoryList = new ArrayList<>();
        rmaAPIService = RmaAPIUtils.getAPIService();

        lvCategory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideKeyboard(view);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("LISTCHOOSE", selectedCategoryList);
                intent.putExtras(bundle);
                setResult(1, intent);
                finish();
            }
        });
    }

    private void hideKeyboard(View view) {
        inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
