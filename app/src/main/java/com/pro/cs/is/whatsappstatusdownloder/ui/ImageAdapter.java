package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.pro.cs.is.whatsappstatusdownloder.R;
import com.pro.cs.is.whatsappstatusdownloder.data.StatusModel;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = "ImageAdapter";

    private final List<StatusModel> imagesList;
    private Context context;
    private ImageFragment imageFragment;

    public ImageAdapter( Context context,List<StatusModel> imagesList, ImageFragment imageFragment) {
        this.context = context;
        this.imagesList = imagesList;
        this.imageFragment = imageFragment;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        StatusModel statusModel = imagesList.get(position);
        holder.ivThumbnail.setImageBitmap(statusModel.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivThumbnail) ImageView ivThumbnail;
        @BindView(R.id.saveToGallery) ImageView saveToGallery;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            saveToGallery.setOnClickListener(view -> {
                StatusModel statusModel = imagesList.get(getAdapterPosition());
                try {
                    imageFragment.downloadImage(statusModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }
}
