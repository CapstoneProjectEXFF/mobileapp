package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Category;

import java.util.ArrayList;

public class CategoryListItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Category> categoryList;
    private OnItemClickListener listener;

    public CategoryListItemAdapter(Context context, ArrayList<Category> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public class ViewHolder {
        public TextView txtCategoryName;
        public CheckBox ckbCategory;
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        CategoryListItemAdapter.ViewHolder viewHolder = null;

        viewHolder = new CategoryListItemAdapter.ViewHolder();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.category_item, null);
        viewHolder.ckbCategory = convertView.findViewById(R.id.ckbCategory);
        viewHolder.txtCategoryName = convertView.findViewById(R.id.txtCategoryName);

        final Category category = (Category) getItem(position);

        viewHolder.txtCategoryName.setText(category.getName());

        if (category.getSupercategoryId() == 0){
            viewHolder.txtCategoryName.setTypeface(viewHolder.txtCategoryName.getTypeface(), Typeface.BOLD);
            viewHolder.ckbCategory.setVisibility(View.GONE);
        }

        if (category.isCheckSelectedCategory()){
            viewHolder.ckbCategory.setChecked(true);
        }

        viewHolder.ckbCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(category);
            }
        });

        return convertView;
    }
}
