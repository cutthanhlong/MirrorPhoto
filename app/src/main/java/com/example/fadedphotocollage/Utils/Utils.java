package com.example.fadedphotocollage.Utils;

import android.os.Debug;

public class Utils {

    public static double getLeftSizeOfMemory() {
        double totalSize = (double) Runtime.getRuntime().maxMemory();
        double heapAllocated = (double) Runtime.getRuntime().totalMemory();
        return (totalSize - (heapAllocated - (double) Runtime.getRuntime().freeMemory())) - (double) Debug.getNativeHeapAllocatedSize();
    }


    public static int maxSizeForSave() {
        int maxSize = (int) Math.sqrt(getLeftSizeOfMemory() / 40.0d);
        return Math.min(maxSize, 1080);
    }

}
