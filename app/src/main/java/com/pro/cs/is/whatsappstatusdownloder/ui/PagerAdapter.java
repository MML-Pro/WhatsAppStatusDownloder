package com.pro.cs.is.whatsappstatusdownloder.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private ImageFragment imageFragment;
    private VideoFragment videoFragment;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        imageFragment = new ImageFragment();
        videoFragment = new VideoFragment();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return imageFragment;
        else
            return videoFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Images";
        else
            return "Videos";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
