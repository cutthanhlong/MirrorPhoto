package com.example.fadedphotocollage.Utils;

import android.os.Debug;

import com.example.fadedphotocollage.R;

public class LibUtility {
    public static int MODE_MULTIPLY;
    public static int MODE_NONE;
    public static int MODE_OVERLAY;
    public static int MODE_SCREEN;
    public static int[] borderRes;
    public static int[] borderResThumb;
    public static int[] filterResThumb;
    public static int[] overlayDrawableList;
    public static int[] overlayResThumb;
    public static int[] textureModes;
    public static int[] textureRes;
    public static int[] textureResThumb;


    public interface ExcludeTabListener {
        void exclude();
    }

    public interface FooterVisibilityListener {
        void setVisibility();
    }

    static {
        borderRes = new int[]{-1, R.drawable.border_0, R.drawable.border_1, R.drawable.border_2, R.drawable.border_3, R.drawable.border_4, R.drawable.border_5, };
        borderResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.border_0_thumb, R.drawable.border_1_thumb, R.drawable.border_2_thumb, R.drawable.border_3_thumb, R.drawable.border_4_thumb, R.drawable.border_5_thumb};
        overlayDrawableList = new int[]{-1, R.drawable.overlay_01, R.drawable.overlay_02, R.drawable.overlay_03, R.drawable.overlay_04, R.drawable.overlay_05};
        overlayResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.overlay_0_thumb, R.drawable.overlay_1_thumb, R.drawable.overlay_2_thumb, R.drawable.overlay_3_thumb, R.drawable.overlay_4_thumb, R.drawable.overlay_5_thumb};
        textureRes = new int[]{-1, R.drawable.texture_01, R.drawable.texture_03, R.drawable.texture_04, R.drawable.texture_05};
        textureResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.texture_0_thumb, R.drawable.texture_1_thumb, R.drawable.texture_2_thumb, R.drawable.texture_3_thumb, R.drawable.texture_4_thumb, R.drawable.texture_5_thumb};
        MODE_NONE = -1;
        MODE_SCREEN = 3;
        MODE_MULTIPLY = 1;
        MODE_OVERLAY = 2;
        textureModes = new int[]{MODE_NONE, MODE_OVERLAY, MODE_SCREEN, MODE_OVERLAY, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_MULTIPLY, MODE_MULTIPLY, MODE_SCREEN, MODE_OVERLAY};
        filterResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.effect_1_thumb, R.drawable.effect_2_thumb, R.drawable.effect_3_thumb, R.drawable.effect_4_thumb, R.drawable.effect_5_thumb, R.drawable.effect_6_thumb, R.drawable.effect_7_thumb, R.drawable.effect_8_thumb, R.drawable.effect_9_thumb, R.drawable.effect_10_thumb, R.drawable.effect_11_thumb, R.drawable.effect_12_thumb, R.drawable.effect_13_thumb, R.drawable.effect_14_thumb, R.drawable.effect_15_thumb, R.drawable.effect_16_thumb, R.drawable.effect_17_thumb, R.drawable.effect_18_thumb, R.drawable.effect_19_thumb, R.drawable.effect_20_thumb, R.drawable.effect_21_thumb, R.drawable.effect_22_thumb};
    }

    public static double getLeftSizeOfMemory() {
        double totalSize = (double) Runtime.getRuntime().maxMemory();
        double heapAllocated = (double) Runtime.getRuntime().totalMemory();
        return (totalSize - (heapAllocated - (double) Runtime.getRuntime().freeMemory())) - (double) Debug.getNativeHeapAllocatedSize();
    }
}
