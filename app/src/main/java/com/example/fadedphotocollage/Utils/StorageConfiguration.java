package com.example.fadedphotocollage.Utils;

import android.os.Environment;

import java.io.File;

public class StorageConfiguration {

    public static final String APP_FOLDER = "Photo Collage";

    private static File createFileIfNeeded(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static File getBaseDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APP_FOLDER);
        return createFileIfNeeded(file);
    }

    public static File getBaseDirectory() {
        return getBaseDir();
    }
}
