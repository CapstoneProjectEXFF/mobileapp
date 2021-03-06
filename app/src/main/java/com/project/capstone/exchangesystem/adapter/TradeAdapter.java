package com.project.capstone.exchangesystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.project.capstone.exchangesystem.model.Image;
import com.project.capstone.exchangesystem.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TradeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> itemList;

    public TradeAdapter(Context context, ArrayList<Item> itemList) {
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
        TradeAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new TradeAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trade_item, null);
            viewHolder.txtNameTradeItem = (TextView) convertView.findViewById(R.id.txtNameTradeItem);
            viewHolder.imgTradeItem = (ImageView) convertView.findViewById(R.id.imgTradeItem);
            viewHolder.checkBoxTrade = (CheckBox) convertView.findViewById(R.id.checkBoxTrade);
            viewHolder.txtTradeIDItem = (TextView) convertView.findViewById(R.id.txtTradeIDItem);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (TradeAdapter.ViewHolder) convertView.getTag();
        }
        Item item = (Item) getItem(position);
        viewHolder.txtNameTradeItem.setText(item.getName());
        viewHolder.txtTradeIDItem.setText(String.valueOf(item.getId()));
        Intent intent = ((Activity) context).getIntent();
//        ArrayList<String> listItem;
//        if (intent.hasExtra("itemMeIdList")) {
//            listItem = intent.getStringArrayListExtra("itemMeIdList");
//        } else {
//            listItem = intent.getStringArrayListExtra("itemYouIdList");
//        }
//        //TODO check listItem null
//        if (listItem != null) {
//            for (int i = 0; i < listItem.size(); i++) {
//                if (listItem.get(i).equals(String.valueOf(item.getId()))) {
//                    viewHolder.checkBoxTrade.setChecked(true);
//                }
//            }
//        }
        String url = "";
        if (item.getImage().size() > 0) {
            url = item.getImage().get(0).getUrl();
            Picasso.with(context).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(viewHolder.imgTradeItem);
        } else {
            viewHolder.imgTradeItem.setImageResource(R.drawable.ic_no_image);
        }
        return convertView;
    }
}
