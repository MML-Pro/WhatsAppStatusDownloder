package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
import java.util.Arrays;
import java.util.Objects;

public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";
    @BindView(R.id.recylerViewVideo)
    RecyclerView recylerView;
    @BindView(R.id.progressBarVideo)
    ProgressBar progressBar;
    private ArrayList<StatusModel> videoModelList;
    private VideoAdapter videoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        videoModelList = new ArrayList<>();
        recylerView.setHasFixedSize(true);
        recylerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        videoAdapter =
                new VideoAdapter(getContext(),
                                videoModelList, VideoFragment.this);
        recylerView.setAdapter(videoAdapter);

        String [] perms = new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                getVideostatus();
            }
        }

    }

    private void getVideostatus() {
        if (MyConstants.STATUS_DIR.exists()) {
            File statusFiles[] = MyConstants.STATUS_DIR.listFiles();
            if (statusFiles != null && statusFiles.length > 0) {
                Arrays.sort(statusFiles);
                for (final File statusFile : statusFiles) {
                    StatusModel statusModel = new
                            StatusModel
                            (statusFile, statusFile.getName(), statusFile.getAbsolutePath());

                    statusModel.setThumbnail(getThumbnail(statusModel));
                    if (statusModel.isVideo()) {
                        videoModelList.add(statusModel);
                    }
                }
                progressBar.setVisibility(View.GONE);
                videoAdapter.notifyDataSetChanged();

            } else {
                Log.e(TAG, "path" + MyConstants.STATUS_DIR);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Dir not exist", Toast.LENGTH_LONG).show();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Do the stuff that requires permission...
                getVideostatus();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show permission explanation dialog...
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Storage permission necessary");
                    alertBuilder.setMessage("The app need storage permission to read the statueses");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    });

                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    //Never ask again selected, or device policy prohibits the app from having that permission.
                    //So, disable that feature, or fall back to another situation...
                }
            }
        }
    }

    public void downloadVideo(StatusModel statusModel) throws IOException {
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

        addVideoToGallery(destFile);

        sourceChannel.close();
        destChannel.close();

    }

    private void addVideoToGallery( File file ) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4"); // or image/png
        Objects.requireNonNull(getContext()).getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

}