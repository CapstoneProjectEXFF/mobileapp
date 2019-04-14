package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.ItemHolder> {

    Context context;
    ArrayList<Item> itemArrayList;
    private OnItemClickListener listener;

    public SelectedItemAdapter(Context context, ArrayList<Item> itemArrayList, OnItemClickListener listener) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public void setfilter(ArrayList<Item> tempArray) {
        itemArrayList.clear();
        if(tempArray != null) {
            itemArrayList.addAll(tempArray);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Item> getfilter() {
        return itemArrayList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item, parent, false);
        ItemHolder itemHolder = new ItemHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Item item = itemArrayList.get(position);
        holder.bind(item, listener);
    }


    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ImageView imgItem;
        public TextView txtNameItem;


        public ItemHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            txtNameItem = itemView.findViewById(R.id.txtNameItem);
        }


        public void bind(final Item item, final OnItemClickListener listener) {
            txtNameItem.setText(item.getName());
            String url = "";
            if (item.getImage().size() > 0) {
                url = item.getImage().get(0).getUrl();
                Picasso.with(context).load(url)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(imgItem);
            } else {
                imgItem.setImageResource(R.drawable.ic_no_image);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }
}
