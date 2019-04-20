package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Category> categoryList;

    private final static int DEFAULT_CHECKED = 1;
    private final static int DEFAULT_UNCHECKED = 0;

    public CategoryListAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public class ViewHolder {
        public TextView txtCategoryName;
        public CheckBox ckbCategory;
        public EditText edtNumOfItem;
    }

    public void setfilter(ArrayList<Category> tempArray) {
        categoryList.clear();
        categoryList.addAll(tempArray);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryListAdapter.ViewHolder viewHolder = null;
//        if (convertView == null) {
            viewHolder = new CategoryListAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_item, null);
            viewHolder.ckbCategory = convertView.findViewById(R.id.ckbCategory);
            viewHolder.txtCategoryName = convertView.findViewById(R.id.txtCategoryName);
            viewHolder.edtNumOfItem = convertView.findViewById(R.id.edtNumOfItem);
//            convertView.setTag(viewHolder);
//
//        } else {
//            viewHolder = (CategoryListAdapter.ViewHolder) convertView.getTag();
//        }
        final Category category = (Category) getItem(position);

        viewHolder.txtCategoryName.setText(category.getName());
        viewHolder.edtNumOfItem.setText("" + category.getNumOfItem());

        if (category.getSupercategoryId() == 0){
            viewHolder.txtCategoryName.setTypeface(viewHolder.txtCategoryName.getTypeface(), Typeface.BOLD);
            viewHolder.ckbCategory.setVisibility(View.GONE);
            viewHolder.edtNumOfItem.setVisibility(View.GONE);
        }

        if (category.isCheckSelectedCategory()){
            viewHolder.ckbCategory.setChecked(true);
        }

        final CategoryListAdapter.ViewHolder tmpViewHolder = viewHolder;

        viewHolder.ckbCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category tmpCategory = categoryList.get(categoryList.indexOf(category));
                if (!tmpCategory.isCheckSelectedCategory()){
                    categoryList.get(categoryList.indexOf(category)).setCheckSelectedCategory(true);
                    categoryList.get(categoryList.indexOf(category)).setNumOfItem(DEFAULT_CHECKED);
                    tmpViewHolder.edtNumOfItem.setText("" + DEFAULT_CHECKED);
                } else {
                    categoryList.get(categoryList.indexOf(category)).setCheckSelectedCategory(false);
                    categoryList.get(categoryList.indexOf(category)).setNumOfItem(DEFAULT_UNCHECKED);
                    tmpViewHolder.edtNumOfItem.setText("" + DEFAULT_UNCHECKED);
                    categoryList.get(categoryList.indexOf(category)).setNumOfItem(Integer.parseInt(tmpViewHolder.edtNumOfItem.getText().toString()));
                }
            }
        });

        viewHolder.edtNumOfItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    try{
                        categoryList.get(categoryList.indexOf(category)).setNumOfItem(Integer.parseInt(tmpViewHolder.edtNumOfItem.getText().toString()));
                    } catch (Exception e){
                        categoryList.get(categoryList.indexOf(category)).setNumOfItem(DEFAULT_UNCHECKED);
                        tmpViewHolder.edtNumOfItem.setHint("" + DEFAULT_UNCHECKED);
                    }

                }
            }
        });

        return convertView;
    }
}
