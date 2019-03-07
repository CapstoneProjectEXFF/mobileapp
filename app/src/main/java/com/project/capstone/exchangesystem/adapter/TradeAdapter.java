package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.CharityPostItem;
import com.project.capstone.exchangesystem.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TradeAdapter extends BaseAdapter {

    private Context context;
    private List<Item> itemList;

    public TradeAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public class ViewHolder {
        public TextView txtNameTradeItem, txtTradeIDItem;
        public ImageView imgTradeItem;
        public CheckBox checkBoxTrade;
//        public TextView txtTradeIDItem;

    }

    public void setfilter(ArrayList<Item> tempArray) {
        //wordlist = new ArrayList<>();
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
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.trade_item, null);
//        }
//        TextView txtNameTradeItem = (TextView) convertView.findViewById(R.id.txtNameTradeItem);
//        txtNameTradeItem.setText(itemList.get(position).getName());
//
//
//        ImageView imgTradeItem = (ImageView) convertView.findViewById(R.id.imgTradeItem);
//        Picasso.with(context).load("https://cdn.tgdd.vn/Products/Images/42/192001/samsung-galaxy-j6-plus-1-400x460.png")
//                .placeholder(R.drawable.no)
//                .error(R.drawable.loadingimage)
//                .into(imgTradeItem);
//
//        CheckBox Chkbox = (CheckBox) convertView.findViewById(R.id.checkBoxTrade);
//        return convertView;


        TradeAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new TradeAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trade_item, null);
            viewHolder.txtNameTradeItem = (TextView) convertView.findViewById(R.id.txtNameTradeItem);
            viewHolder.imgTradeItem = (ImageView) convertView.findViewById(R.id.imgTradeItem);
            viewHolder.checkBoxTrade = (CheckBox) convertView.findViewById(R.id.checkBoxTrade);
//            viewHolder.txtTradeIDItem = (TextView) convertView.findViewById(R.id.txtTradeIDItem);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (TradeAdapter.ViewHolder) convertView.getTag();
        }
        Item item = (Item) getItem(position);
        viewHolder.txtNameTradeItem.setText(item.getName());
//        viewHolder.txtTradeIDItem.setText(item.getId());
        Picasso.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSds7xM5V2GKMhmwIdQNAWProLwB1-cIZwnS7nYtnyMkcosV1b3IQ")
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(viewHolder.imgTradeItem);
        return convertView;
    }
}
