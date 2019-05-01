package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.capstone.exchangesystem.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageHolder> {
    Context context;
    List<String> urlList;
    private final OnItemClickListener listener;

    public GalleryAdapter(Context context, List<String> urlList, OnItemClickListener listener) {
        this.context = context;
        this.urlList = urlList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public void setfilter(List<String> newUrlList) {
        urlList.clear();
        urlList.addAll(newUrlList);
        notifyDataSetChanged();
    }

    public List<String> getfilter() {
        return urlList;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_gallery_item, parent, false);
        ImageHolder imageHolder = new ImageHolder(v);
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        String url = urlList.get(position);
        holder.bind(url, listener);
    }


    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;


        public ImageHolder(View imageView) {
            super(imageView);
            ivImage = imageView.findViewById(R.id.ivImage);
        }


        public void bind(final String url, final OnItemClickListener listener) {
            Picasso.with(context).load(url)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(url);
                }
            });
        }
    }
}
