package com.pro.cs.is.whatsappstatusdownloder.Utils;

import android.os.Environment;

import java.io.File;

public class MyConstants {

    public static final File STATUS_DIR = new File(Environment.getExternalStorageDirectory()
            + File.separator + "WhatsApp/Media/.Statuses/");
    public static final File APP_DIR = new File("/storage/emulated/0/","WhatsAppStatusProDir");

    public static final short THUMB_SIZE = 128;

}
