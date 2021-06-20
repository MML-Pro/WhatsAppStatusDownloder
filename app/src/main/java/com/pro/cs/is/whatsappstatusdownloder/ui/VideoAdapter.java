package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.content.Context;
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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<StatusModel> videoList;
    private Context context;
    private VideoFragment videoFragment;

    public VideoAdapter(Context context, List<StatusModel> videoList, VideoFragment videoFragment) {
        this.context = context;
        this.videoList = videoList;
        this.videoFragment = videoFragment;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new VideoAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        StatusModel statusModel = videoList.get(position);
        holder.ivThumbnail.setImageBitmap(statusModel.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivThumbnail) ImageView ivThumbnail;
        @BindView(R.id.saveToGallery) ImageView saveToGallery;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            saveToGallery.setOnClickListener(view -> {
                StatusModel statusModel = videoList.get(getAdapterPosition());
                try {
                    videoFragment.downloadVideo(statusModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }
}
