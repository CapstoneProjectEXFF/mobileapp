package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPostTarget;
import com.project.capstone.exchangesystem.model.TargetStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DonationTargetAdapter extends RecyclerView.Adapter<DonationTargetAdapter.ViewHolder>{
    Context context;
    List<DonationPostTarget> targets;
    ArrayList<TargetStatus> targetStatusList;

    public DonationTargetAdapter(Context context, List<DonationPostTarget> targets, ArrayList<TargetStatus> targetStatusList) {
        this.context = context;
        this.targets = targets;
        this.targetStatusList = targetStatusList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.target_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        DonationPostTarget target = targets.get(position);
        viewHolder.bind(target);
    }

    @Override
    public long getItemId(int position) {
        return targets.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return targets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItemName, txtTarget;
        public ProgressBar pbTarget;

        public ViewHolder(@NonNull View view) {
            super(view);
            txtTarget = view.findViewById(R.id.txtTarget);
            txtItemName = view.findViewById(R.id.txtItemName);
            pbTarget = view.findViewById(R.id.pbTarget);
        }

        public void bind(DonationPostTarget target) {
            txtItemName.setText(target.getCategory().getName());

            int tmpPosition = targets.indexOf(target);
            TargetStatus targetStatus = targetStatusList.get(tmpPosition);
            txtTarget.setText(targetStatus.getCount() + "/" + target.getTarget());

            pbTarget.setMax(target.getTarget());
            pbTarget.setProgress(targetStatus.getCount());
        }
    }
}
