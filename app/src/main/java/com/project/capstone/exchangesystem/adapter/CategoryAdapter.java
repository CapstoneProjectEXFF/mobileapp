package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    Context context;
    List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
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
        return categoryList.get(position).getId();
    }

    public class ViewHolder {
        TextView txtName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryAdapter.ViewHolder viewHolder = null;
        Category category = categoryList.get(position);

        viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = layoutInflater.inflate(R.layout.spinner_item, null);
        viewHolder.txtName = convertView.findViewById(R.id.txtName);
        viewHolder.txtName.setText(category.getName());

        if (category.getSupercategoryId() == 0){
            viewHolder.txtName.setTypeface(viewHolder.txtName.getTypeface(), Typeface.BOLD);
        }

        return convertView;

    }
}
