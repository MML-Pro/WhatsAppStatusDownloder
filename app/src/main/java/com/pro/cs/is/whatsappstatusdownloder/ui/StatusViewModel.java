package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pro.cs.is.whatsappstatusdownloder.Utils.MyConstants;
import com.pro.cs.is.whatsappstatusdownloder.data.StatusModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {

    Context mContext;
    ProgressBar mProgressBar;
    ArrayList<StatusModel> statusImages;
    ImageAdapter mImageAdapter;
    MutableLiveData<ArrayList<StatusModel>> statusMutableLiveData = new MutableLiveData<>();



    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public ProgressBar getmProgressBar() {
        return mProgressBar;
    }

    public void setmProgressBar(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    public ArrayList<StatusModel> getStatusImages() {
        return statusImages;
    }

    public void setStatusImages(ArrayList<StatusModel> statusImages) {
        this.statusImages = statusImages;
    }

    public ImageAdapter getmImageAdapter() {
        return mImageAdapter;
    }

    public void setmImageAdapter(ImageAdapter mImageAdapter) {
        this.mImageAdapter = mImageAdapter;
    }

    public void getImageStatus() {
        if (MyConstants.STATUS_DIR.exists()) {
            File[] statusFiles = MyConstants.STATUS_DIR.listFiles();
            if (statusFiles != null && statusFiles.length > 0) {
                Arrays.sort(statusFiles);
                for (final File statusFile : statusFiles) {
                    StatusModel statusModel = new
                            StatusModel
                            (statusFile, statusFile.getName(), statusFile.getAbsolutePath());

                    statusModel.setThumbnail(getThumbnail(statusModel));
                    if (!statusModel.isVideo()) {
                        getStatusImages().add(statusModel);
                    }
                }
                statusMutableLiveData.setValue(getStatusImages());
                getmProgressBar().setVisibility(View.GONE);
                getmImageAdapter().notifyDataSetChanged();

            } else {
                getmProgressBar().setVisibility(View.GONE);
                Toast.makeText(getmContext(), "Dir not exist", Toast.LENGTH_LONG).show();
            }

        }
    }

    private Bitmap getThumbnail(StatusModel statusModel) {
        if (statusModel.isVideo()) {
            return ThumbnailUtils.createVideoThumbnail(statusModel.getFile().getAbsoluteFile().toString(),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        } else {
            return ThumbnailUtils.
                    extractThumbnail
                            (BitmapFactory.decodeFile(statusModel.getFile().getAbsolutePath()),
                                    MyConstants.THUMB_SIZE, MyConstants.THUMB_SIZE);
        }
    }


}
