package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pro.cs.is.whatsappstatusdownloder.R;
import com.pro.cs.is.whatsappstatusdownloder.Utils.MyConstants;
import com.pro.cs.is.whatsappstatusdownloder.data.StatusModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment {

    private static final String TAG = "ImageFragment";
    @BindView(R.id.recylerView)
    RecyclerView recylerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<StatusModel> imagesModelList;
    private ImageAdapter imageAdapter;
    StatusViewModel statusViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        imagesModelList = new ArrayList<>();
        recylerView.setHasFixedSize(true);
        recylerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        imageAdapter =
                new ImageAdapter
                        (getContext(),
                                imagesModelList, ImageFragment.this);
        recylerView.setAdapter(imageAdapter);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        statusViewModel = ViewModelProviders.of(this).get(StatusViewModel.class);
        statusViewModel.setmContext(getContext());
        statusViewModel.setStatusImages(imagesModelList);
        statusViewModel.setmImageAdapter(imageAdapter);
        statusViewModel.setmProgressBar(progressBar);

        statusViewModel.getImageStatus();

        statusViewModel.statusMutableLiveData.observe(this, statusModels -> {
            imagesModelList.clear();
            imagesModelList.addAll(statusModels);
            imageAdapter.notifyDataSetChanged();
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Do the stuff that requires permission...
                statusViewModel.getStatusImages();

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show permission explanation dialog...
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Storage permission necessary");
                    alertBuilder.setMessage("The app need storage permission to read the statueses");
                    alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which)
                            -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1));

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                }
            }
        }
    }

    public void downloadImage(StatusModel statusModel) throws IOException {
        File file = MyConstants.APP_DIR;
        if (!file.exists()) {
            boolean result = file.mkdirs();
            Log.e(TAG, "resultOfFileMkdirs: "+result );
        }
        File destFile = new File(file + File.separator + statusModel.getTitle());

        if (destFile.exists()) {
            destFile.delete();
        }

        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel sourceChannel = new FileInputStream(statusModel.getFile()).getChannel();
        FileChannel destChannel = new FileOutputStream(destFile).getChannel();

        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        Toast.makeText(getActivity(), "done!", Toast.LENGTH_LONG).show();

        Log.e(TAG, "destFile: "+destFile.getPath() );

        addImageGallery(destFile);

        sourceChannel.close();
        destChannel.close();

    }

    private void addImageGallery( File file ) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // or image/png
        Objects.requireNonNull(getContext()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}