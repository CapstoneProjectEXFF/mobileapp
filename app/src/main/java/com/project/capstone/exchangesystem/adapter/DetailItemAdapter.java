package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> itemList;

    public DetailItemAdapter(Context context, ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public class ViewHolder {
        public TextView txtNameItem;
        public ImageView imgItem;
    }

    public void setfilter(ArrayList<Item> tempArray) {
        itemList.clear();
        itemList.addAll(tempArray);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailItemAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new DetailItemAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trans_detail_item, null);
            viewHolder.txtNameItem = convertView.findViewById(R.id.txtNameItem);
            viewHolder.imgItem = convertView.findViewById(R.id.imgItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DetailItemAdapter.ViewHolder) convertView.getTag();
        }
        Item item = (Item) getItem(position);
        viewHolder.txtNameItem.setText(item.getName());

        String url = item.getImage().get(0).getUrl();
            Picasso.with(context).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(viewHolder.imgItem);
        return convertView;
    }
}