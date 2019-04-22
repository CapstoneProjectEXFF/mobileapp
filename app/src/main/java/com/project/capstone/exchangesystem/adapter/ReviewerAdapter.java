package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Rate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewerAdapter extends RecyclerView.Adapter<ReviewerAdapter.ViewHolder> {
    Context context;
    ArrayList<Rate> ratingList;
    private final OnItemClickListener listener;

    public ReviewerAdapter(Context context, ArrayList<Rate> ratingList, OnItemClickListener listener) {
        this.context = context;
        this.ratingList = ratingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_rating_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Rate rate = ratingList.get(position);
        viewHolder.bind(rate, listener);
    }

    @Override
    public long getItemId(int position) {
        return ratingList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNameReviewer, txtContent;
        public ImageButton star1, star2, star3, star4, star5;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtNameReviewer = view.findViewById(R.id.txtNameReviewer);
            txtContent = view.findViewById(R.id.txtContent);
            star1 = view.findViewById(R.id.star1);
            star2 = view.findViewById(R.id.star2);
            star3 = view.findViewById(R.id.star3);
            star4 = view.findViewById(R.id.star4);
            star5 = view.findViewById(R.id.star5);
        }

        public void bind(Rate rate, OnItemClickListener listener) {
            //TODO load avatar

            txtNameReviewer.setText(rate.getSender().getFullName());
            txtContent.setText(rate.getContent());

            switch (rate.getRate()){
                case 1:
                    star1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    star1.setVisibility(View.VISIBLE);
                    star2.setVisibility(View.VISIBLE);
                    star3.setVisibility(View.VISIBLE);
                    star4.setVisibility(View.VISIBLE);
                    star5.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Rate rate);
    }
}