package com.project.capstone.exchangesystem.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.CategoryListAdapter;
import com.project.capstone.exchangesystem.adapter.CategoryListItemAdapter;
import com.project.capstone.exchangesystem.model.Category;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import java.util.ArrayList;

public class ChooseCategoryItemActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvCategory;
    ArrayList<Category> selectedCategoryList;
    RmaAPIService rmaAPIService;
    Context context;
    CategoryListItemAdapter categoryListAdapter;
    Category selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category_item);

        getComponents();
        setToolbar();
        setCategoryData();
    }

    private void getComponents() {
        context = this;
        toolbar = findViewById(R.id.tbToolbar);
        lvCategory = findViewById(R.id.lvCategory);
        selectedCategoryList = new ArrayList<>();
        rmaAPIService = RmaAPIUtils.getAPIService();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnSelectedCategory(selectedCategory);
            }
        });
    }

    private void setCategoryData() {
        selectedCategoryList = (ArrayList<Category>) getIntent().getSerializableExtra("selectedCategory");
        selectedCategory = (Category) getIntent().getSerializableExtra("selectedCategoryPos");

        categoryListAdapter = new CategoryListItemAdapter(context, selectedCategoryList, new CategoryListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                if (selectedCategory != null){
                    selectedCategoryList.get(selectedCategory.getId() - 1).setCheckSelectedCategory(false);
                }

                if (category != selectedCategory){
                    selectedCategoryList.get(selectedCategoryList.indexOf(category)).setCheckSelectedCategory(true);
                    selectedCategory = category;
                    returnSelectedCategory(selectedCategory);
                }
            }
        });
        lvCategory.setAdapter(categoryListAdapter);
    }

    private void returnSelectedCategory(Category category) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("LISTCHOOSE", selectedCategoryList);
        bundle.putSerializable("selectedCategory", category);
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();
    }
}
