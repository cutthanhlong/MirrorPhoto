package com.example.fadedphotocollage.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fadedphotocollage.Bitmap.BitmapResizer;
import com.example.fadedphotocollage.Image.ImageLoader;
import com.example.fadedphotocollage.R;
import com.example.fadedphotocollage.Utils.Analytics;
import com.example.fadedphotocollage.Utils.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    int MEDIA_TYPE_IMAGE = 1;

    String IMAGE_DIRECTORY_NAME = "Photo Editor";

    int REQUEST_MIRROR = 3;
    Analytics analytics;
    int PERMISSION_MIRROR_EDITOR = 55;

    Uri fileUri;
    LinearLayout linearMirror;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        analytics = new Analytics(this);

        initViews();
        imageLoader = new ImageLoader(this);
        imageLoader.setListener(new ImageLoader.ImageLoaded() {
            @Override
            public void callFileSizeAlertDialogBuilder() {
                fileSizeAlertDialogBuilder();
            }
        });
    }

    private void initViews() {
        linearMirror = findViewById(R.id.linearMirror);

        linearMirror.setOnClickListener(this);
    }

    private void fileSizeAlertDialogBuilder() {
        startShaderActivity();
    }

    private void startShaderActivity() {
        int maxSize = Utility.maxSizeForDimension(this, 1, 1500.0f);
        Intent shaderIntent = new Intent(getApplicationContext(), MirrorImageActivity.class);
        shaderIntent.putExtra("selectedImagePath", this.imageLoader.selectedImagePath);
        shaderIntent.putExtra("isSession", false);
        shaderIntent.putExtra("MAX_SIZE", maxSize);
        Utility.logFreeMemory(this);
        startActivity(shaderIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_MIRROR_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == REQUEST_MIRROR) {
                try {
                    imageLoader.getImageFromIntent(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeScreenActivity.this, "" + getString(R.string.error_img_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (linearMirror == v) {
            if (analytics != null)
                analytics.logEvent(Analytics.Param.MENU_MIRROR, "");
            if (Build.VERSION.SDK_INT < 23) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
            } else {
                if (checkAndRequestMirrorPermissions()) {
                    //If you have already permitted the permission
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
                }
            }
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    public File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private boolean checkAndRequestMirrorPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writestoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writestoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_MIRROR_EDITOR);
            return false;
        }
        return true;
    }
}
