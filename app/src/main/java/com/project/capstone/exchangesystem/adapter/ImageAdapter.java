package com.project.capstone.exchangesystem.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Image;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    Context context;
    ArrayList<Image> imageArrayList;
    private final OnItemClickListener listener;

    public ImageAdapter(Context context, ArrayList<Image> imageArrayList, OnItemClickListener listener) {
        this.context = context;
        this.imageArrayList = imageArrayList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Image image);
    }

    public void setfilter(ArrayList<Image> newImageList) {
        imageArrayList.clear();
        imageArrayList.addAll(newImageList);
        notifyDataSetChanged();
    }

    public ArrayList<Image> getfilter() {
        return imageArrayList;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image, parent, false);
        ImageHolder imageHolder = new ImageHolder(v);
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Image image = imageArrayList.get(position);
        holder.bind(image, listener);
    }


    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView ivSeletecImage;


        public ImageHolder(View imageView) {
            super(imageView);
            ivSeletecImage = imageView.findViewById(R.id.ivSeletecImage);
        }


        public void bind(final Image image, final OnItemClickListener listener) {
            if (image.getUri() != null) {
                try {
                    //TODO handle oversize of image
                    Bitmap bmp = (Bitmap) MediaStore.Images.Media.getBitmap(context.getContentResolver(), image.getUri());
                    ivSeletecImage.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (image.getUrl() != null){
                Picasso.with(context).load(image.getUrl())
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(ivSeletecImage);
            } else {
                ivSeletecImage.setImageResource(R.drawable.ic_no_image);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(image);
                }
            });
        }

    }
}
