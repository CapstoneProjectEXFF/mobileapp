package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.squareup.picasso.Picasso;
import com.project.capstone.exchangesystem.model.CharityPostItem;

import java.util.ArrayList;

public class MainCharityPostAdapter extends BaseAdapter {
    Context context;
    ArrayList<DonationPost> donationPosts;

    public MainCharityPostAdapter(Context context, ArrayList<DonationPost> donationPosts) {
        this.context = context;
        this.donationPosts = donationPosts;
    }

    @Override
    public int getCount() {
        return donationPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return donationPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder {
        public TextView txtContent, txtNameCharity, txtTimestamp;
        public ImageView imgCharityPost, imgProfileCharity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainCharityPostAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.charity_post_feed_item, null);
            viewHolder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
            viewHolder.txtTimestamp = (TextView) convertView.findViewById(R.id.txtTimestamp);
            viewHolder.txtNameCharity = (TextView) convertView.findViewById(R.id.txtNameCharity);
            viewHolder.imgCharityPost = (ImageView) convertView.findViewById(R.id.imgCharityPost);
            viewHolder.imgProfileCharity = (ImageView) convertView.findViewById(R.id.imgProfileCharity);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (MainCharityPostAdapter.ViewHolder) convertView.getTag();
        }
        DonationPost donationPost = (DonationPost) getItem(position);
        viewHolder.txtNameCharity.setText(donationPost.getContent().substring(0,15));

//        viewHolder.txtContent.setText(charityPostItem.getContent());
//        Log.d("Test ", product.getDescription() + product.getName());
        viewHolder.txtContent.setMaxLines(2);
        viewHolder.txtContent.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.txtContent.setText(donationPost.getContent());
        Picasso.with(context).load(donationPost.getImages().get(0).getUrl())
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(viewHolder.imgCharityPost);
        Picasso.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSds7xM5V2GKMhmwIdQNAWProLwB1-cIZwnS7nYtnyMkcosV1b3IQ")
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(viewHolder.imgProfileCharity);
        return convertView;
    }
}
