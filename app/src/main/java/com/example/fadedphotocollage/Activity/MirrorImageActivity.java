package com.example.fadedphotocollage.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.example.fadedphotocollage.Adapter.MyRecyclerViewAdapter;
import com.example.fadedphotocollage.Bitmap.BitmapResizer;
import com.example.fadedphotocollage.CanvasTextView.ApplyTextInterface;
import com.example.fadedphotocollage.CanvasTextView.CustomRelativeLayout;
import com.example.fadedphotocollage.CanvasTextView.SingleTapInterface;
import com.example.fadedphotocollage.CanvasTextView.TextDataItem;
import com.example.fadedphotocollage.Fragments.EffectFragment;
import com.example.fadedphotocollage.Fragments.WriteTextFragment;
import com.example.fadedphotocollage.Utils.Analytics;
import com.example.fadedphotocollage.Utils.LibUtility;
import com.example.fadedphotocollage.Utils.MirrorImageMode;
import com.example.fadedphotocollage.Utils.StorageConfiguration;
import com.example.fadedphotocollage.Utils.Utils;
import com.example.fadedphotocollage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MirrorImageActivity extends AppCompatActivity {

    public static final int INDEX_0 = 0;
    public static final int INDEX_1 = 1;
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    public static final int INDEX_6 = 6;
    public static final int INDEX_7 = 7;
    private static final String TAG = "MirrorImageActivity";
    Analytics analytics;

    CustomRelativeLayout customRelativeLayout;
    int currentSelectedTabIndex = -1;
    ImageView[] d3ButtonArray;
    EffectFragment effectFragment;
    Bitmap filterBitmap;
    WriteTextFragment writeTextFragment;
    int initialYPos = 0;
    RelativeLayout mainLayout;
    ImageView[] mirrorButtonArray;
    MirrorView mirrorView;
    float mulX = 16;
    float mulY = 16;
    Button[] ratioButtonArray;
    AlertDialog saveImageAlert;
    int screenHeightPixels;
    int screenWidthPixels;
    boolean showText = false;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    Bitmap sourceBitmap;
    View[] tabButtonList;
    ArrayList<TextDataItem> textDataList = new ArrayList<>();
    ViewFlipper viewFlipper;
    Matrix matrix1 = new Matrix();
    Matrix matrix2 = new Matrix();
    Matrix matrix3 = new Matrix();
    Matrix matrix4 = new Matrix();

    private final int[] d3resList = new int[]{R.drawable.img_test, R.drawable.img_test, R.drawable.mirror_3d_10, R.drawable.mirror_3d_10,
            R.drawable.mirror_3d_11, R.drawable.mirror_3d_11, R.drawable.mirror_3d_4, R.drawable.mirror_3d_4, R.drawable.mirror_3d_3,
            R.drawable.mirror_3d_3, R.drawable.mirror_3d_1, R.drawable.mirror_3d_1, R.drawable.mirror_3d_6, R.drawable.mirror_3d_6,
            R.drawable.mirror_3d_13, R.drawable.mirror_3d_13, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15,
            R.drawable.mirror_3d_15, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16};

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror_image);

        getWindow().addFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        Bundle extras = getIntent().getExtras();
        sourceBitmap = BitmapResizer.decodeBitmapFromFile(extras.getString("selectedImagePath"), extras.getInt("MAX_SIZE"));
        if (sourceBitmap == null) {
            Toast msg = Toast.makeText(MirrorImageActivity.this, "Could not load the photo, please use another GALLERY app!", Toast.LENGTH_LONG);
            msg.setGravity(17, msg.getXOffset() / INDEX_2, msg.getYOffset() / INDEX_2);
            msg.show();
            finish();
            return;
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeightPixels = metrics.heightPixels;
        screenWidthPixels = metrics.widthPixels;
        Display display = getWindowManager().getDefaultDisplay();
        if (screenWidthPixels <= 0) {
            screenWidthPixels = display.getWidth();
        }
        if (screenHeightPixels <= 0) {
            screenHeightPixels = display.getHeight();
        }

        mirrorView = new MirrorView(MirrorImageActivity.this, screenWidthPixels, screenHeightPixels);

        mainLayout = findViewById(R.id.layout_mirror_activity);
        mainLayout.addView(mirrorView);
        viewFlipper = findViewById(R.id.mirror_view_flipper);
        viewFlipper.bringToFront();
        findViewById(R.id.mirror_footer).bringToFront();
        slideLeftIn = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_in_left);
        slideLeftOut = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_out_left);
        slideRightIn = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_in_right);
        slideRightOut = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_out_right);
        analytics = new Analytics(this);
        findViewById(R.id.mirror_header).bringToFront();
        addEffectFragment();
        setSelectedTab(0);
    }

    WriteTextFragment.FontChoosedListener fontChoosedListener = new WriteTextFragment.FontChoosedListener() {
        @Override
        public void onOk(TextDataItem textData) {
            customRelativeLayout.addTextView(textData);
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        }
    };

    void addEffectFragment() {
        if (effectFragment == null) {
            effectFragment = (EffectFragment) getSupportFragmentManager().findFragmentByTag("MY_EFFECT_FRAGMENT");
            if (effectFragment == null) {
                effectFragment = new EffectFragment();
                Log.e(TAG, "EffectFragment == null");
                effectFragment.setBitmap(sourceBitmap);
                effectFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.mirror_effect_fragment_container, effectFragment, "MY_EFFECT_FRAGMENT").commit();
            } else {
                effectFragment.setBitmap(sourceBitmap);
                effectFragment.setSelectedTabIndex(INDEX_0);
            }
            effectFragment.setBitmapReadyListener(new EffectFragment.BitmapReady() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    filterBitmap = bitmap;
                    mirrorView.postInvalidate();
                }
            });
            effectFragment.setBorderIndexChangedListener(new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
                @Override
                public void onIndexChanged(int i) {
                    mirrorView.setFrame(i);
                }
            });
        }
    }

    final class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {
        private final MediaScannerConnection mConn;
        private final String mFilename;

        public MyMediaScannerConnectionClient(Context ctx, File file, String mimetype) {
            this.mFilename = file.getAbsolutePath();
            this.mConn = new MediaScannerConnection(ctx, this);
            this.mConn.connect();
        }

        public void onMediaScannerConnected() {
            this.mConn.scanFile(this.mFilename, "");
        }

        public void onScanCompleted(String path, Uri uri) {
            this.mConn.disconnect();
        }
    }

    private class SaveImageTask extends AsyncTask<Object, Object, Object> {
        ProgressDialog progressDialog;
        String resultPath;

        private SaveImageTask() {
            this.resultPath = null;
        }

        protected Object doInBackground(Object... arg0) {
            this.resultPath = mirrorView.saveBitmap(true, screenWidthPixels, screenHeightPixels);
            return null;
        }

        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(MirrorImageActivity.this);
            this.progressDialog.setMessage("Saving image ...");
            this.progressDialog.show();
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.cancel();
            }
            if (this.resultPath != null) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MirrorImageActivity.this, SaveShareImageActivity.class);
                        intent.putExtra("imagePath", resultPath);
                        startActivity(intent);
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
        }
        if (filterBitmap != null) {
            filterBitmap.recycle();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("show_text", this.showText);
        savedInstanceState.putSerializable("text_data", this.textDataList);
        if (writeTextFragment != null && writeTextFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showText = savedInstanceState.getBoolean("show_text");
        textDataList = (ArrayList) savedInstanceState.getSerializable("text_data");
        if (textDataList == null) {
            textDataList = new ArrayList<>();
        }
    }

    public void myClickHandler(View view) {
        int id = view.getId();
        this.mirrorView.drawSavedImage = false;
        if (id == R.id.button_save_mirror_image) {
            if (analytics != null)
            new SaveImageTask().execute();
        } else if (id == R.id.closeScreen) {
            backButtonAlertBuilder();
        } else if (id == R.id.button_mirror) {
            if (analytics != null)
                setSelectedTab(INDEX_0);
        } else if (id == R.id.button_mirror_frame) {
            setSelectedTab(INDEX_4);
        } else if (id == R.id.button_mirror_ratio) {
            setSelectedTab(INDEX_2);
        } else if (id == R.id.button_mirror_effect) {
            setSelectedTab(INDEX_3);
        } else if (id == R.id.button_mirror_adj) {
            setSelectedTab(INDEX_5);
        } else if (id == R.id.button_mirror_3d) {
            setSelectedTab(INDEX_1);
        } else if (id == R.id.button_3d_1) {
            set3dMode(INDEX_0);
        } else if (id == R.id.button_3d_2) {
            set3dMode(INDEX_1);
        } else if (id == R.id.button_3d_3) {
            set3dMode(INDEX_2);
        } else if (id == R.id.button_3d_4) {
            set3dMode(INDEX_3);
        } else if (id == R.id.button_3d_5) {
            set3dMode(INDEX_4);
        } else if (id == R.id.button_3d_6) {
            set3dMode(INDEX_5);
        } else if (id == R.id.button_3d_7) {
            set3dMode(INDEX_6);
        } else if (id == R.id.button_3d_8) {
            set3dMode(INDEX_7);
        } else if (id == R.id.button_3d_9) {
            set3dMode(8);
        } else if (id == R.id.button_3d_10) {
            set3dMode(9);
        } else if (id == R.id.button_3d_11) {
            set3dMode(10);
        } else if (id == R.id.button_3d_12) {
            set3dMode(11);
        } else if (id == R.id.button_3d_13) {
            set3dMode(12);
        } else if (id == R.id.button_3d_14) {
            set3dMode(13);
        } else if (id == R.id.button_3d_15) {
            set3dMode(14);
        } else if (id == R.id.button_3d_16) {
            set3dMode(15);
        } else if (id == R.id.button_3d_17) {
            set3dMode(16);
        } else if (id == R.id.button_3d_18) {
            set3dMode(17);
        } else if (id == R.id.button_3d_19) {
            set3dMode(18);
        } else if (id == R.id.button_3d_20) {
            set3dMode(19);
        } else if (id == R.id.button_3d_21) {
            set3dMode(20);
        } else if (id == R.id.button_3d_22) {
            set3dMode(21);
        } else if (id == R.id.button_3d_23) {
            set3dMode(22);
        } else if (id == R.id.button_3d_24) {
            set3dMode(23);
        } else if (id == R.id.button11) {
            this.mulX = 1.0f;
            this.mulY = 1.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_0);
        } else if (id == R.id.button21) {
            this.mulX = 2.0f;
            this.mulY = 1.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_1);
        } else if (id == R.id.button12) {
            this.mulX = 1.0f;
            this.mulY = 2.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_2);
        } else if (id == R.id.button32) {
            this.mulX = 3.0f;
            this.mulY = 2.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_3);
        } else if (id == R.id.button23) {
            this.mulX = 2.0f;
            this.mulY = 3.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_4);
        } else if (id == R.id.button43) {
            this.mulX = 4.0f;
            this.mulY = 3.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_5);
        } else if (id == R.id.button34) {
            this.mulX = 3.0f;
            this.mulY = 4.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_6);
        } else if (id == R.id.button45) {
            this.mulX = 4.0f;
            this.mulY = 5.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_7);
        } else if (id == R.id.button57) {
            this.mulX = 5.0f;
            this.mulY = 7.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(8);
        } else if (id == R.id.button169) {
            this.mulX = 16.0f;
            this.mulY = 9.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(9);
        } else if (id == R.id.button916) {
            this.mulX = 9.0f;
            this.mulY = 16.0f;
            this.mirrorView.reset(screenWidthPixels, screenHeightPixels, true);
            setRatioButtonBg(10);
        } else if (id == R.id.button_m1) {
            this.mirrorView.setCurrentMode(INDEX_0);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_0);
        } else if (id == R.id.button_m2) {
            this.mirrorView.setCurrentMode(INDEX_1);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_1);
        } else if (id == R.id.button_m3) {
            this.mirrorView.setCurrentMode(INDEX_2);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_2);
        } else if (id == R.id.button_m4) {
            this.mirrorView.setCurrentMode(INDEX_3);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_3);
        } else if (id == R.id.button_m5) {
            this.mirrorView.setCurrentMode(INDEX_4);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_4);
        } else if (id == R.id.button_m6) {
            this.mirrorView.setCurrentMode(INDEX_5);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_5);
        } else if (id == R.id.button_m7) {
            this.mirrorView.setCurrentMode(INDEX_6);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_6);
        } else if (id == R.id.button_m8) {
            this.mirrorView.setCurrentMode(INDEX_7);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_7);
        } else if (id == R.id.button_m9) {
            this.mirrorView.setCurrentMode(8);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(8);
        } else if (id == R.id.button_m10) {
            this.mirrorView.setCurrentMode(9);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(9);
        } else if (id == R.id.button_m11) {
            this.mirrorView.setCurrentMode(10);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(10);
        } else if (id == R.id.button_m12) {
            this.mirrorView.setCurrentMode(11);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(11);
        } else if (id == R.id.button_m13) {
            this.mirrorView.setCurrentMode(12);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(12);
        } else if (id == R.id.button_m14) {
            this.mirrorView.setCurrentMode(13);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(13);
        } else if (id == R.id.button_m15) {
            this.mirrorView.setCurrentMode(14);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(14);
        } else if (id == R.id.button_mirror_text) {
//            addCanvasTextView();
//            clearViewFlipper();
        } else {
            this.effectFragment.myClickHandler(id);
            if (id == R.id.buttonCancel || id == R.id.buttonOk) {
                clearFxAndFrame();
            }
        }
    }

    private void clearFxAndFrame() {
        int selectedTabIndex = this.effectFragment.getSelectedTabIndex();
        if (this.currentSelectedTabIndex != INDEX_3 && this.currentSelectedTabIndex != INDEX_4) {
            return;
        }
        if (selectedTabIndex == 0 || selectedTabIndex == INDEX_1) {
//            clearViewFlipper();
        }
    }

    /*void addCanvasTextView() {
        customRelativeLayout = new CustomRelativeLayout(MirrorImageActivity.this, textDataList, mirrorView.f510I, new SingleTapInterface() {

            @Override
            public void onSingleTap(TextDataItem textData) {
                writeTextFragment = new WriteTextFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("text_data", textData);
                writeTextFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.text_view_fragment_container, writeTextFragment, "FONT_FRAGMENT").commit();
                Log.e(MirrorImageActivity.TAG, "replace fragment");
                writeTextFragment.setFontChoosedListener(fontChoosedListener);
            }
        });
        customRelativeLayout.setApplyTextListener(new ApplyTextInterface() {
            @Override
            public void onCancel() {
                showText = true;
                mainLayout.removeView(customRelativeLayout);
                mirrorView.postInvalidate();
            }

            @Override
            public void onOk(ArrayList<TextDataItem> arrayList) {
                for (TextDataItem textDataItem : arrayList) {
                    textDataItem.setImageSaveMatrix(mirrorView.f510I);
                }
                textDataList = arrayList;
                showText = true;
                if (mainLayout == null) {
                    mainLayout = findViewById(R.id.layout_mirror_activity);
                }
                mainLayout.removeView(customRelativeLayout);
                mirrorView.postInvalidate();
            }
        });
        showText = false;
        mirrorView.invalidate();
        mainLayout.addView(customRelativeLayout);
        findViewById(R.id.text_view_fragment_container).bringToFront();
        writeTextFragment = new WriteTextFragment();
        writeTextFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().add(R.id.text_view_fragment_container, writeTextFragment, "FONT_FRAGMENT").commit();
        Log.e(TAG, "add fragment");
        writeTextFragment.setFontChoosedListener(this.fontChoosedListener);
    }*/

    private void set3dMode(int index) {
        mirrorView.d3Mode = true;
        if (index > 15 && index < 20) {
            mirrorView.setCurrentMode(index);
        } else if (index > 19) {
            mirrorView.setCurrentMode(index - 4);
        } else if (index % INDEX_2 == 0) {
            mirrorView.setCurrentMode(INDEX_0);
        } else {
            mirrorView.setCurrentMode(INDEX_1);
        }
        this.mirrorView.reset(screenWidthPixels, screenHeightPixels, false);
        loadInBitmap(this.d3resList[index]);
        mirrorView.postInvalidate();
        setD3ButtonBg(index);
    }

    @SuppressLint({"NewApi"})
    private void loadInBitmap(int resId) {
        Log.e(TAG, "loadInBitmap");
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (mirrorView.d3Bitmap == null || mirrorView.d3Bitmap.isRecycled()) {
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            BitmapFactory.decodeResource(getResources(), resId, options);
            mirrorView.d3Bitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = INDEX_1;
        options.inBitmap = mirrorView.d3Bitmap;
        try {
            mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            if (!(mirrorView.d3Bitmap == null || mirrorView.d3Bitmap.isRecycled())) {
                mirrorView.d3Bitmap.recycle();
            }
            mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), resId);
        }
    }

    private void setD3ButtonBg(int index) {
        if (d3ButtonArray == null) {
            d3ButtonArray = new ImageView[24];
            d3ButtonArray[0] = findViewById(R.id.button_3d_1);
            d3ButtonArray[1] = findViewById(R.id.button_3d_2);
            d3ButtonArray[2] = findViewById(R.id.button_3d_3);
            d3ButtonArray[3] = findViewById(R.id.button_3d_4);
            d3ButtonArray[4] = findViewById(R.id.button_3d_5);
            d3ButtonArray[5] = findViewById(R.id.button_3d_6);
            d3ButtonArray[6] = findViewById(R.id.button_3d_7);
            d3ButtonArray[7] = findViewById(R.id.button_3d_8);
            d3ButtonArray[8] = findViewById(R.id.button_3d_9);
            d3ButtonArray[9] = findViewById(R.id.button_3d_10);
            d3ButtonArray[10] = findViewById(R.id.button_3d_11);
            d3ButtonArray[11] = findViewById(R.id.button_3d_12);
            d3ButtonArray[12] = findViewById(R.id.button_3d_13);
            d3ButtonArray[13] = findViewById(R.id.button_3d_14);
            d3ButtonArray[14] = findViewById(R.id.button_3d_15);
            d3ButtonArray[15] = findViewById(R.id.button_3d_16);
            d3ButtonArray[16] = findViewById(R.id.button_3d_17);
            d3ButtonArray[17] = findViewById(R.id.button_3d_18);
            d3ButtonArray[18] = findViewById(R.id.button_3d_19);
            d3ButtonArray[19] = findViewById(R.id.button_3d_20);
            d3ButtonArray[20] = findViewById(R.id.button_3d_21);
            d3ButtonArray[21] = findViewById(R.id.button_3d_22);
            d3ButtonArray[22] = findViewById(R.id.button_3d_23);
            d3ButtonArray[23] = findViewById(R.id.button_3d_24);
        }
        for (int i = 0; i < 24; i++) {
            d3ButtonArray[i].setBackgroundColor(getResources().getColor(R.color.primary));
        }
        d3ButtonArray[index].setBackgroundColor(getResources().getColor(R.color.footer_button_color_pressed));
    }

    private void setMirrorButtonBg(int index) {
        if (mirrorButtonArray == null) {
            mirrorButtonArray = new ImageView[15];
            mirrorButtonArray[0] = findViewById(R.id.button_m1);
            mirrorButtonArray[1] = findViewById(R.id.button_m2);
            mirrorButtonArray[2] = findViewById(R.id.button_m3);
            mirrorButtonArray[3] = findViewById(R.id.button_m4);
            mirrorButtonArray[4] = findViewById(R.id.button_m5);
            mirrorButtonArray[5] = findViewById(R.id.button_m6);
            mirrorButtonArray[6] = findViewById(R.id.button_m7);
            mirrorButtonArray[7] = findViewById(R.id.button_m8);
            mirrorButtonArray[8] = findViewById(R.id.button_m9);
            mirrorButtonArray[9] = findViewById(R.id.button_m10);
            mirrorButtonArray[10] = findViewById(R.id.button_m11);
            mirrorButtonArray[11] = findViewById(R.id.button_m12);
            mirrorButtonArray[12] = findViewById(R.id.button_m13);
            mirrorButtonArray[13] = findViewById(R.id.button_m14);
            mirrorButtonArray[14] = findViewById(R.id.button_m15);
        }
        for (int i = 0; i < 15; i += INDEX_1) {
            mirrorButtonArray[i].setBackgroundResource(R.color.button_gray);
        }
        mirrorButtonArray[index].setBackgroundResource(R.color.mirror_button_color);
    }

    private void setRatioButtonBg(int index) {
        if (ratioButtonArray == null) {
            ratioButtonArray = new Button[11];
            ratioButtonArray[0] = findViewById(R.id.button11);
            ratioButtonArray[1] = findViewById(R.id.button21);
            ratioButtonArray[2] = findViewById(R.id.button12);
            ratioButtonArray[3] = findViewById(R.id.button32);
            ratioButtonArray[4] = findViewById(R.id.button23);
            ratioButtonArray[5] = findViewById(R.id.button43);
            ratioButtonArray[6] = findViewById(R.id.button34);
            ratioButtonArray[7] = findViewById(R.id.button45);
            ratioButtonArray[8] = findViewById(R.id.button57);
            ratioButtonArray[9] = findViewById(R.id.button169);
            ratioButtonArray[10] = findViewById(R.id.button916);
        }
        for (int i = 0; i < 11; i += INDEX_1) {
            ratioButtonArray[i].setBackgroundResource(R.drawable.selector_collage_ratio_button);
        }
        ratioButtonArray[index].setBackgroundResource(R.drawable.collage_ratio_bg_pressed);
    }

    void setSelectedTab(int index) {
        setTabBg(INDEX_0);

        int displayedChild = viewFlipper.getDisplayedChild();
        if (index == 0) {
            if (displayedChild != 0) {
                viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.setDisplayedChild(INDEX_0);
            } else {
                return;
            }
        }
        if (index == INDEX_1) {
            setTabBg(INDEX_1);
            if (displayedChild != INDEX_1) {
                if (displayedChild == 0) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                viewFlipper.setDisplayedChild(INDEX_1);
            } else {
                return;
            }
        }
        if (index == INDEX_2) {
            setTabBg(INDEX_2);
            if (displayedChild != INDEX_2) {
                if (displayedChild == 0) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                this.viewFlipper.setDisplayedChild(INDEX_2);
            } else {
                return;
            }
        }
        if (index == INDEX_3) {
            setTabBg(INDEX_3);
            effectFragment.setSelectedTabIndex(INDEX_0);
            if (displayedChild != INDEX_3) {
                if (displayedChild == 0 || displayedChild == INDEX_2) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                viewFlipper.setDisplayedChild(INDEX_3);
            } else {
                return;
            }
        }
        if (index == INDEX_4) {
            setTabBg(INDEX_4);
            effectFragment.setSelectedTabIndex(INDEX_1);
            if (displayedChild != INDEX_3) {
                if (displayedChild == INDEX_5) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                } else {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                }
                viewFlipper.setDisplayedChild(INDEX_3);
            } else {
                return;
            }
        }
        if (index == INDEX_5) {
            setTabBg(INDEX_5);
            effectFragment.showToolBar();
            if (displayedChild != INDEX_3) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.setDisplayedChild(INDEX_3);
            } else {
                return;
            }
        }
        if (index == INDEX_7) {
            setTabBg(-1);
            if (displayedChild != INDEX_4) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.setDisplayedChild(INDEX_4);
            }
        }
    }

    private void setTabBg(int index) {
        currentSelectedTabIndex = index;
        if (tabButtonList == null) {
            tabButtonList = new View[INDEX_6];
            tabButtonList[INDEX_0] = findViewById(R.id.button_mirror);
            tabButtonList[INDEX_1] = findViewById(R.id.button_mirror_3d);
            tabButtonList[INDEX_3] = findViewById(R.id.button_mirror_effect);
            tabButtonList[INDEX_2] = findViewById(R.id.button_mirror_ratio);
            tabButtonList[INDEX_4] = findViewById(R.id.button_mirror_frame);
            tabButtonList[INDEX_5] = findViewById(R.id.button_mirror_adj);
        }
        for (View view : tabButtonList) {
            view.setBackgroundResource(R.drawable.collage_footer_button);
        }
        if (index >= 0) {
            tabButtonList[index].setBackgroundResource(R.color.footer_button_color_pressed);
        }
    }

    /*void clearViewFlipper() {
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.setDisplayedChild(INDEX_4);
        setTabBg(-1);
    }*/

    public void onBackPressed() {
        /*if (writeTextFragment != null && writeTextFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        } else if (viewFlipper.getDisplayedChild() == INDEX_3) {
            clearFxAndFrame();
            clearViewFlipper();
        } else if (!showText && customRelativeLayout != null) {
            showText = true;
            mainLayout.removeView(customRelativeLayout);
            mirrorView.postInvalidate();
            customRelativeLayout = null;
            Log.e(TAG, "replace fragment");
        } else if (viewFlipper.getDisplayedChild() != INDEX_4) {
            clearViewFlipper();
        } else {
            backButtonAlertBuilder();
        }*/
    }

    private void backButtonAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MirrorImageActivity.this);
        builder.setMessage("Would you like to save image ?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SaveImageTask().execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        saveImageAlert = builder.create();
        saveImageAlert.show();
    }

    class MirrorView extends View {
        final Matrix f510I;
        int currentModeIndex;
        Bitmap d3Bitmap;
        boolean d3Mode;
        int defaultColor;
        RectF destRect1;
        RectF destRect1X;
        RectF destRect1Y;
        RectF destRect2;
        RectF destRect2X;
        RectF destRect2Y;
        RectF destRect3;
        RectF destRect4;
        boolean drawSavedImage;
        RectF dstRectPaper1;
        RectF dstRectPaper2;
        RectF dstRectPaper3;
        RectF dstRectPaper4;
        Bitmap frameBitmap;
        Paint framePaint;
        int height;
        boolean isTouchStartedLeft;
        boolean isTouchStartedTop;
        boolean isVerticle;
        Matrix m1;
        Matrix m2;
        Matrix m3;
        MirrorImageMode[] mirrorModeList;
        MirrorImageMode modeX;
        MirrorImageMode modeX10;
        MirrorImageMode modeX11;
        MirrorImageMode modeX12;
        MirrorImageMode modeX13;
        MirrorImageMode modeX14;
        MirrorImageMode modeX15;
        MirrorImageMode modeX16;
        MirrorImageMode modeX17;
        MirrorImageMode modeX18;
        MirrorImageMode modeX19;
        MirrorImageMode modeX2;
        MirrorImageMode modeX20;
        MirrorImageMode modeX3;
        MirrorImageMode modeX4;
        MirrorImageMode modeX5;
        MirrorImageMode modeX6;
        MirrorImageMode modeX7;
        MirrorImageMode modeX8;
        MirrorImageMode modeX9;
        float oldX;
        float oldY;
        RectF srcRect1;
        RectF srcRect2;
        RectF srcRect3;
        RectF srcRectPaper;
        int tMode1;
        int tMode2;
        int tMode3;
        Matrix textMatrix;
        Paint textRectPaint;
        RectF totalArea1;
        RectF totalArea2;
        RectF totalArea3;
        int width;

        public MirrorView(Context context, int screenWidth, int screenHeight) {
            super(context);
            f510I = new Matrix();
            framePaint = new Paint();
            isVerticle = false;
            defaultColor = R.color.bg;
            mirrorModeList = new MirrorImageMode[20];
            currentModeIndex = INDEX_0;
            drawSavedImage = false;
            d3Mode = false;
            textMatrix = new Matrix();
            textRectPaint = new Paint(INDEX_1);
            m1 = new Matrix();
            m2 = new Matrix();
            m3 = new Matrix();
            width = sourceBitmap.getWidth();
            height = sourceBitmap.getHeight();
            int widthPixels = screenWidth;
            int heightPixels = screenHeight;
            createMatrix(widthPixels, heightPixels);
            createRectX(widthPixels, heightPixels);
            createRectY(widthPixels, heightPixels);
            createRectXY(widthPixels, heightPixels);
            createModes();
            framePaint.setAntiAlias(true);
            framePaint.setFilterBitmap(true);
            framePaint.setDither(true);
            textRectPaint.setColor(getResources().getColor(R.color.bg));
        }

        private void reset(int widthPixels, int heightPixels, boolean invalidate) {
            createMatrix(widthPixels, heightPixels);
            createRectX(widthPixels, heightPixels);
            createRectY(widthPixels, heightPixels);
            createRectXY(widthPixels, heightPixels);
            createModes();
            if (invalidate) {
                postInvalidate();
            }
        }

        private String saveBitmap(boolean saveToFile, int widthPixel, int heightPixel) {
            int i;
            float minDimen = (float) Math.min(widthPixel, heightPixel);
            float upperScale = (float) Utils.maxSizeForSave();
            float scale = upperScale / minDimen;
            Log.e(MirrorImageActivity.TAG, "upperScale" + upperScale);
            Log.e(MirrorImageActivity.TAG, "scale" + scale);
            if (mulY > mulX) {
                float f = mulX;
                float r0 = 1.0f;
                scale = (r0 * scale) / mulY;
            }
            if (scale <= 0.0f) {
                scale = 1.0f;
            }
            Log.e(MirrorImageActivity.TAG, "scale" + scale);
            int wP = Math.round(((float) widthPixel) * scale);
            int wH = Math.round(((float) heightPixel) * scale);
            RectF srcRect = mirrorModeList[currentModeIndex].getSrcRect();
            reset(wP, wH, false);
            int btmWidth = Math.round(mirrorView.getCurrentMirrorMode().rectTotalArea.width());
            int btmHeight = Math.round(mirrorView.getCurrentMirrorMode().rectTotalArea.height());
            if (btmWidth % MirrorImageActivity.INDEX_2 == MirrorImageActivity.INDEX_1) {
                btmWidth--;
            }
            if (btmHeight % MirrorImageActivity.INDEX_2 == MirrorImageActivity.INDEX_1) {
                btmHeight--;
            }
            Bitmap savedBitmap = Bitmap.createBitmap(btmWidth, btmHeight, Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(savedBitmap);
            Matrix matrix = new Matrix();
            matrix.reset();
            Log.e(MirrorImageActivity.TAG, "btmWidth " + btmWidth);
            Log.e(MirrorImageActivity.TAG, "btmHeight " + btmHeight);
            matrix.postTranslate(((float) (-(wP - btmWidth))) / 2.0f, ((float) (-(wH - btmHeight))) / 2.0f);
            MirrorImageMode saveMode = mirrorModeList[currentModeIndex];
            saveMode.setSrcRect(srcRect);
            if (MirrorImageActivity.this.filterBitmap == null) {
                drawMode(bitmapCanvas, sourceBitmap, saveMode, matrix);
            } else {
                drawMode(bitmapCanvas, filterBitmap, saveMode, matrix);
            }
            if (d3Mode && this.d3Bitmap != null) {
                if (!d3Bitmap.isRecycled()) {
                    bitmapCanvas.setMatrix(matrix);
                    bitmapCanvas.drawBitmap(d3Bitmap, null, mirrorModeList[currentModeIndex].rectTotalArea, framePaint);
                }
            }
            Matrix mat;
            if (MirrorImageActivity.this.textDataList != null) {
                i = MirrorImageActivity.INDEX_0;
                while (true) {
                    if (i >= MirrorImageActivity.this.textDataList.size()) {
                        break;
                    }
                    mat = new Matrix();
                    mat.set(MirrorImageActivity.this.textDataList.get(i).imageSaveMatrix);
                    mat.postScale(scale, scale);
                    mat.postTranslate(((float) (-(wP - btmWidth))) / 2.0f, ((float) (-(wH - btmHeight))) / 2.0f);
                    bitmapCanvas.setMatrix(mat);
                    bitmapCanvas.drawText(textDataList.get(i).message, textDataList.get(i).xPos, textDataList.get(i).yPos, textDataList.get(i).textPaint);
                    i += MirrorImageActivity.INDEX_1;
                }
            }
            if (frameBitmap != null) {
                if (!frameBitmap.isRecycled()) {
                    bitmapCanvas.setMatrix(matrix);
                    bitmapCanvas.drawBitmap(frameBitmap, null, mirrorModeList[currentModeIndex].rectTotalArea, framePaint);
                }
            }
            String resultPath = null;
            if (saveToFile) {
                String twitterUploadFile = String.valueOf(System.currentTimeMillis());
                resultPath = new StringBuilder(StorageConfiguration.getBaseDirectory().toString()).append(MirrorImageActivity.this.getString(R.string.directory)).append(twitterUploadFile).append(".jpg").toString();
                new File(resultPath).getParentFile().mkdirs();
                try {
                    FileOutputStream out = new FileOutputStream(resultPath);
                    savedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            savedBitmap.recycle();
            reset(widthPixel, heightPixel, false);
            mirrorModeList[currentModeIndex].setSrcRect(srcRect);
            return resultPath;
        }

        private void setCurrentMode(int index) {
            currentModeIndex = index;
        }

        public MirrorImageMode getCurrentMirrorMode() {
            return mirrorModeList[currentModeIndex];
        }

        private void createModes() {
            modeX = new MirrorImageMode(INDEX_4, srcRect3, destRect1, destRect1, destRect3, destRect3, matrix1, f510I, matrix1, tMode3, totalArea3);
            modeX2 = new MirrorImageMode(INDEX_4, srcRect3, destRect1, destRect4, destRect1, destRect4, matrix1, matrix1, f510I, tMode3, totalArea3);
            modeX3 = new MirrorImageMode(INDEX_4, srcRect3, destRect3, destRect2, destRect3, destRect2, matrix1, matrix1, f510I, tMode3, totalArea3);
            modeX8 = new MirrorImageMode(INDEX_4, srcRect3, destRect1, destRect1, destRect1, destRect1, matrix1, matrix2, matrix3, tMode3, totalArea3);
            int m9TouchMode = INDEX_4;
            if (tMode3 == 0) {
                m9TouchMode = INDEX_0;
            }
            modeX9 = new MirrorImageMode(INDEX_4, srcRect3, destRect2, destRect2, destRect2, destRect2, matrix1, matrix2, matrix3, m9TouchMode, totalArea3);
            int m10TouchMode = INDEX_3;
            if (tMode3 == INDEX_1) {
                m10TouchMode = INDEX_1;
            }
            modeX10 = new MirrorImageMode(INDEX_4, srcRect3, destRect3, destRect3, destRect3, destRect3, matrix1, matrix2, matrix3, m10TouchMode, totalArea3);
            int m11TouchMode = INDEX_4;
            if (tMode3 == 0) {
                m11TouchMode = INDEX_3;
            }
            modeX11 = new MirrorImageMode(INDEX_4, srcRect3, destRect4, destRect4, destRect4, destRect4, matrix1, matrix2, matrix3, m11TouchMode, totalArea3);
            modeX4 = new MirrorImageMode(INDEX_2, srcRect1, destRect1X, destRect1X, matrix1, tMode1, totalArea1);
            int m5TouchMode = INDEX_4;
            if (this.tMode1 == 0) {
                m5TouchMode = INDEX_0;
            } else if (tMode1 == INDEX_5) {
                m5TouchMode = INDEX_5;
            }
            modeX5 = new MirrorImageMode(INDEX_2, srcRect1, destRect2X, destRect2X, matrix1, m5TouchMode, totalArea1);
            modeX6 = new MirrorImageMode(INDEX_2, srcRect2, destRect1Y, destRect1Y, matrix2, tMode2, totalArea2);
            int m7TouchMode = INDEX_3;
            if (tMode2 == INDEX_1) {
                m7TouchMode = INDEX_1;
            } else if (tMode2 == INDEX_6) {
                m7TouchMode = INDEX_6;
            }
            modeX7 = new MirrorImageMode(INDEX_2, srcRect2, destRect2Y, destRect2Y, matrix2, m7TouchMode, totalArea2);
            modeX12 = new MirrorImageMode(INDEX_2, srcRect1, destRect1X, destRect2X, matrix4, tMode1, totalArea1);
            modeX13 = new MirrorImageMode(INDEX_2, srcRect2, destRect1Y, destRect2Y, matrix4, tMode2, totalArea2);
            modeX14 = new MirrorImageMode(INDEX_2, srcRect1, destRect1X, destRect1X, matrix3, tMode1, totalArea1);
            modeX15 = new MirrorImageMode(INDEX_2, srcRect2, destRect1Y, destRect1Y, matrix3, tMode2, totalArea2);
            modeX16 = new MirrorImageMode(INDEX_4, srcRectPaper, dstRectPaper1, dstRectPaper2, dstRectPaper3, dstRectPaper4, matrix1, matrix1, f510I, tMode1, totalArea1);
            modeX17 = new MirrorImageMode(INDEX_4, srcRectPaper, dstRectPaper1, dstRectPaper3, dstRectPaper3, dstRectPaper1, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX18 = new MirrorImageMode(INDEX_4, srcRectPaper, dstRectPaper2, dstRectPaper4, dstRectPaper2, dstRectPaper4, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX19 = new MirrorImageMode(INDEX_4, srcRectPaper, dstRectPaper1, dstRectPaper2, dstRectPaper2, dstRectPaper1, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX20 = new MirrorImageMode(INDEX_4, srcRectPaper, dstRectPaper4, dstRectPaper3, dstRectPaper3, dstRectPaper4, f510I, matrix1, matrix1, tMode1, totalArea1);
            mirrorModeList[INDEX_0] = modeX4;
            mirrorModeList[INDEX_1] = modeX5;
            mirrorModeList[INDEX_2] = modeX6;
            mirrorModeList[INDEX_3] = modeX7;
            mirrorModeList[INDEX_4] = modeX8;
            mirrorModeList[INDEX_5] = modeX9;
            mirrorModeList[INDEX_6] = modeX10;
            mirrorModeList[INDEX_7] = modeX11;
            mirrorModeList[8] = modeX12;
            mirrorModeList[9] = modeX13;
            mirrorModeList[10] = modeX14;
            mirrorModeList[11] = modeX15;
            mirrorModeList[12] = modeX;
            mirrorModeList[13] = modeX2;
            mirrorModeList[14] = modeX3;
            mirrorModeList[15] = modeX7;
            mirrorModeList[16] = modeX17;
            mirrorModeList[17] = modeX18;
            mirrorModeList[18] = modeX19;
            mirrorModeList[19] = modeX20;
        }

        public Bitmap getBitmap() {
            setDrawingCacheEnabled(true);
            buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(getDrawingCache());
            setDrawingCacheEnabled(false);
            return bmp;
        }

        public void setFrame(int index) {
            if (!(this.frameBitmap == null || this.frameBitmap.isRecycled())) {
                this.frameBitmap.recycle();
                this.frameBitmap = null;
            }
            if (index == 0) {
                postInvalidate();
                return;
            }
            this.frameBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.borderRes[index]);
            postInvalidate();
        }

        private void createMatrix(int widthPixels, int heightPixels) {
            f510I.reset();
            matrix1.reset();
            matrix1.postScale(-1.0f, 1.0f);
            matrix1.postTranslate((float) widthPixels, 0.0f);
            matrix2.reset();
            matrix2.postScale(1.0f, -1.0f);
            matrix2.postTranslate(0.0f, (float) heightPixels);
            matrix3.reset();
            matrix3.postScale(-1.0f, -1.0f);
            matrix3.postTranslate((float) widthPixels, (float) heightPixels);
        }

        private void createRectX(int widthPixels, int heightPixels) {
            float destH = ((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX);
            float destW = ((float) widthPixels) / 2.0f;
            float destX = 0.0f;
            float destY;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) initialYPos) + ((((float) heightPixels) - destH) / 2.0f);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.destRect1X = new RectF(destX, destY, destW + destX, destH + destY);
            float destXX = destX + destW;
            this.destRect2X = new RectF(destXX, destY, destW + destXX, destH + destY);
            this.totalArea1 = new RectF(destX, destY, destW + destXX, destH + destY);
            this.tMode1 = MirrorImageActivity.INDEX_1;
            if (MirrorImageActivity.this.mulX * ((float) this.height) <= (MirrorImageActivity.this.mulY * 2.0f) * ((float) this.width)) {
                srcX = (((float) this.width) - (((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height)) / 2.0f)) / 2.0f;
                srcX2 = srcX + (((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height)) / 2.0f);
            } else {
                srcY = (((float) this.height) - (((float) (this.width * MirrorImageActivity.INDEX_2)) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX))) / 2.0f;
                srcY2 = srcY + (((float) (this.width * MirrorImageActivity.INDEX_2)) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX));
                this.tMode1 = MirrorImageActivity.INDEX_5;
            }
            this.srcRect1 = new RectF(srcX, srcY, srcX2, srcY2);
            this.srcRectPaper = new RectF(srcX, srcY, ((srcX2 - srcX) / 2.0f) + srcX, srcY2);
            float destWPapar = destW / 2.0f;
            this.dstRectPaper1 = new RectF(destX, destY, destWPapar + destX, destH + destY);
            float dextXP = destX + destWPapar;
            this.dstRectPaper2 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
            dextXP += destWPapar;
            this.dstRectPaper3 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
            dextXP += destWPapar;
            this.dstRectPaper4 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
        }

        private void createRectY(int widthPixels, int heightPixels) {
            float destH = (((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX)) / 2.0f;
            float destW = (float) widthPixels;
            float destX = 0.0f;
            float destY;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) MirrorImageActivity.this.initialYPos) + ((((float) heightPixels) - (2.0f * destH)) / 2.0f);
            this.destRect1Y = new RectF(destX, destY, destW + destX, destH + destY);
            float destYY = destY + destH;
            this.destRect2Y = new RectF(destX, destYY, destW + destX, destH + destYY);
            this.totalArea2 = new RectF(destX, destY, destW + destX, destH + destYY);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.tMode2 = MirrorImageActivity.INDEX_0;
            if ((MirrorImageActivity.this.mulX * 2.0f) * ((float) this.height) > MirrorImageActivity.this.mulY * ((float) this.width)) {
                srcY = (((float) this.height) - (((MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX) * ((float) this.width)) / 2.0f)) / 2.0f;
                srcY2 = srcY + (((MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX) * ((float) this.width)) / 2.0f);
            } else {
                srcX = (((float) this.width) - (((float) (this.height * MirrorImageActivity.INDEX_2)) * (MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY))) / 2.0f;
                srcX2 = srcX + (((float) (this.height * MirrorImageActivity.INDEX_2)) * (MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY));
                this.tMode2 = MirrorImageActivity.INDEX_6;
            }
            this.srcRect2 = new RectF(srcX, srcY, srcX2, srcY2);
        }

        private void createRectXY(int widthPixels, int heightPixels) {
            float destH = (((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX)) / 2.0f;
            float destW = ((float) widthPixels) / 2.0f;
            float destX = 0.0f;
            float destY = (float) MirrorImageActivity.this.initialYPos;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) MirrorImageActivity.this.initialYPos) + ((((float) heightPixels) - (2.0f * destH)) / 2.0f);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.destRect1 = new RectF(destX, destY, destW + destX, destH + destY);
            float destX2 = destX + destW;
            this.destRect2 = new RectF(destX2, destY, destW + destX2, destH + destY);
            float destY2 = destY + destH;
            this.destRect3 = new RectF(destX, destY2, destW + destX, destH + destY2);
            this.destRect4 = new RectF(destX2, destY2, destW + destX2, destH + destY2);
            this.totalArea3 = new RectF(destX, destY, destW + destX2, destH + destY2);
            if (MirrorImageActivity.this.mulX * ((float) this.height) <= MirrorImageActivity.this.mulY * ((float) this.width)) {
                srcX = (((float) this.width) - ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height))) / 2.0f;
                srcX2 = srcX + ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height));
                this.tMode3 = MirrorImageActivity.INDEX_1;
            } else {
                srcY = (((float) this.height) - (((float) this.width) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX))) / 2.0f;
                srcY2 = srcY + (((float) this.width) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX));
                this.tMode3 = MirrorImageActivity.INDEX_0;
            }
            this.srcRect3 = new RectF(srcX, srcY, srcX2, srcY2);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawColor(this.defaultColor);
            if (MirrorImageActivity.this.filterBitmap == null) {
                drawMode(canvas, MirrorImageActivity.this.sourceBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            } else {
                drawMode(canvas, MirrorImageActivity.this.filterBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            }
            if (!(!this.d3Mode || this.d3Bitmap == null || this.d3Bitmap.isRecycled())) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.d3Bitmap, null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            if (MirrorImageActivity.this.showText) {
                for (int i = MirrorImageActivity.INDEX_0; i < MirrorImageActivity.this.textDataList.size(); i += MirrorImageActivity.INDEX_1) {
                    this.textMatrix.set(MirrorImageActivity.this.textDataList.get(i).imageSaveMatrix);
                    this.textMatrix.postConcat(this.f510I);
                    canvas.setMatrix(this.textMatrix);
                    canvas.drawText(MirrorImageActivity.this.textDataList.get(i).message, MirrorImageActivity.this.textDataList.get(i).xPos, MirrorImageActivity.this.textDataList.get(i).yPos, MirrorImageActivity.this.textDataList.get(i).textPaint);
                    canvas.setMatrix(this.f510I);
                    canvas.drawRect(0.0f, 0.0f, this.mirrorModeList[this.currentModeIndex].rectTotalArea.left, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                    canvas.drawRect(0.0f, 0.0f, (float) MirrorImageActivity.this.screenWidthPixels, this.mirrorModeList[this.currentModeIndex].rectTotalArea.top, this.textRectPaint);
                    canvas.drawRect(this.mirrorModeList[this.currentModeIndex].rectTotalArea.right, 0.0f, (float) MirrorImageActivity.this.screenWidthPixels, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                    canvas.drawRect(0.0f, this.mirrorModeList[this.currentModeIndex].rectTotalArea.bottom, (float) MirrorImageActivity.this.screenWidthPixels, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                }
            }
            if (!(this.frameBitmap == null || this.frameBitmap.isRecycled())) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.frameBitmap, null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            super.onDraw(canvas);
        }

        private void drawMode(Canvas canvas, Bitmap bitmap, MirrorImageMode mirrorMode, Matrix matrix) {
            canvas.setMatrix(matrix);
            canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect1, this.framePaint);
            this.m1.set(mirrorMode.matrix1);
            this.m1.postConcat(matrix);
            canvas.setMatrix(this.m1);
            if (!(bitmap == null || bitmap.isRecycled())) {
                canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect2, this.framePaint);
            }
            if (mirrorMode.count == MirrorImageActivity.INDEX_4) {
                this.m2.set(mirrorMode.matrix2);
                this.m2.postConcat(matrix);
                canvas.setMatrix(this.m2);
                if (!(bitmap == null || bitmap.isRecycled())) {
                    canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect3, this.framePaint);
                }
                this.m3.set(mirrorMode.matrix3);
                this.m3.postConcat(matrix);
                canvas.setMatrix(this.m3);
                if (bitmap != null && !bitmap.isRecycled()) {
                    canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect4, this.framePaint);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MirrorImageActivity.INDEX_0 /*0*/:
                    this.isTouchStartedLeft = x < ((float) (MirrorImageActivity.this.screenWidthPixels / MirrorImageActivity.INDEX_2));
                    this.isTouchStartedTop = y < ((float) (MirrorImageActivity.this.screenHeightPixels / MirrorImageActivity.INDEX_2));
                    this.oldX = x;
                    this.oldY = y;
                    break;
                case MirrorImageActivity.INDEX_2 /*2*/:
                    moveGrid(this.mirrorModeList[this.currentModeIndex].getSrcRect(), x - this.oldX, y - this.oldY);
                    this.mirrorModeList[this.currentModeIndex].updateBitmapSrc();
                    this.oldX = x;
                    this.oldY = y;
                    break;
            }
            postInvalidate();
            return true;
        }

        void moveGrid(RectF srcRect, float x, float y) {
            if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_1 || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_4 || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_6) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_4) {
                    x *= -1.0f;
                }
                if (this.isTouchStartedLeft && this.mirrorModeList[this.currentModeIndex].touchMode != MirrorImageActivity.INDEX_6) {
                    x *= -1.0f;
                }
                if (srcRect.left + x < 0.0f) {
                    x = -srcRect.left;
                }
                if (srcRect.right + x >= ((float) this.width)) {
                    x = ((float) this.width) - srcRect.right;
                }
                srcRect.left += x;
                srcRect.right += x;
            } else if (this.mirrorModeList[this.currentModeIndex].touchMode == 0 || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_3 || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_5) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_3) {
                    y *= -1.0f;
                }
                if (this.isTouchStartedTop && this.mirrorModeList[this.currentModeIndex].touchMode != MirrorImageActivity.INDEX_5) {
                    y *= -1.0f;
                }
                if (srcRect.top + y < 0.0f) {
                    y = -srcRect.top;
                }
                if (srcRect.bottom + y >= ((float) this.height)) {
                    y = ((float) this.height) - srcRect.bottom;
                }
                srcRect.top += y;
                srcRect.bottom += y;
            }
        }
    }
}
