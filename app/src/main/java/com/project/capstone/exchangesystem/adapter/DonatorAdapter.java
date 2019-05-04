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
import com.project.capstone.exchangesystem.model.Donator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DonatorAdapter extends RecyclerView.Adapter<DonatorAdapter.ViewHolder> {
    Context context;
    ArrayList<Donator> donators;
    private final OnItemClickListener listener;

    public DonatorAdapter(Context context, ArrayList<Donator> donators, OnItemClickListener listener) {
        this.context = context;
        this.donators = donators;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.donator_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Donator donator = donators.get(position);
        viewHolder.bind(donator, listener);
    }

    @Override
    public long getItemId(int position) {
        return donators.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return donators.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDonatorAvatar;
        public TextView txtNameDonator, txtNameDonatedItems;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivDonatorAvatar = view.findViewById(R.id.ivDonatorAvatar);
            txtNameDonator = view.findViewById(R.id.txtNameDonator);
            txtNameDonatedItems = view.findViewById(R.id.txtNameDonatedItems);
        }

        public void bind(Donator donator, OnItemClickListener listener) {
            txtNameDonator.setText(donator.getDonatorName());
//            String nameDonatedItems = "";
//            for (int i = 0; i < donator.getItemNames().size(); i++){
//                if (i == (donator.getItemNames().size() - 1)){
//                    nameDonatedItems += donator.getItemNames().get(i);
//                } else {
//                    nameDonatedItems += donator.getItemNames().get(i) + ", ";
//                }
//            }
//            txtNameDonatedItems.setText(nameDonatedItems);

            txtNameDonatedItems.setText("Quyên góp " + donator.getItemNames().size() + " đồ dùng.");
            Picasso.with(context).load(donator.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(ivDonatorAvatar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Donator donator);
    }
}
