package com.example.fadedphotocollage.Utils;

import android.content.Context;






public class Analytics {
    public static class Param {
        public static final String POST = "POST";
        public static final String MENU_COLLAGE = "MENU_COLLAGE";
        public static final String MENU_EDITOR = "MENU_EDITOR";
        public static final String MENU_SCRAPBOOK = "MENU_SCRAPBOOK";
        public static final String MENU_CAMERA = "MENU_CAMERA";
        public static final String MENU_MIRROR = "MENU_MIRROR";
        public static final String EDITOR_LAYOUT = "EDITOR_LAYOUT";
        public static final String EDITOR_BACKGROUND = "EDITOR_BACKGROUND";
        public static final String EDITOR_SPACE = "EDITOR_SPACE";
        public static final String EDITOR_RATIO = "EDITOR_RATIO";
        public static final String EDITOR_TEXT = "EDITOR_TEXT";
        public static final String EDITOR_FILTER = "EDITOR_FILTER";
        public static final String MENU_RATEUS = "MENU_RATEUS";
        public static final String MENU_SHARE_APP = "MENU_SHARE_APP";
        public static final String EDITOR_FILTER_FX = "EDITOR_FILTER_FX";
        public static final String EDITOR_FILTER_FRAME = "EDITOR_FILTER_FRAME";
        public static final String EDITOR_FILTER_LIGHT = "EDITOR_FILTER_LIGHT";
        public static final String EDITOR_FILTER_TEXTURE = "EDITOR_FILTER_TEXTURE";
        public static final String EDITOR_FILTER_BLUR = "EDITOR_FILTER_BLUR";
        public static final String EDITOR_FILTER_RESET = "EDITOR_FILTER_RESET";
        public static final String IMAGE_SAVE = "IMAGE_SAVE";
        public static String MIRROR_MENU[] = {"INDEX_MIRROR", "INDEX_MIRROR_3D", "INDEX_MIRROR_RATIO", "INDEX_MIRROR_EFFECT", "INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX", "INDEX_MIRROR_ADJUSTMENT", "TAB_SIZE", "INDEX_MIRROR_INVISIBLE_VIEW"};
        public static final String MIRROR_SAVE_IMAGE = "MIRROR_SAVE_IMAGE";

    }



    public Analytics(Context context) {


    }

    public void logEvent(String event, String type) {


    }

}
