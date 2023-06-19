package com.example.fadedphotocollage.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fadedphotocollage.Bitmap.BitmapLoader;
import com.example.fadedphotocollage.Image.ImageUtility;
import com.example.fadedphotocollage.R;

import java.io.File;
import java.util.List;


public class SaveShareImageActivity extends AppCompatActivity {

    private Bundle bundle;
    private String imagePath;
    private ImageView shareImageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_share_image);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            imagePath = bundle.getString("imagePath");
        }

        shareImageview = (ImageView) findViewById(R.id.share_imageView);
        new BitmapWorkerTask().execute();
        // Set a toolbar to replace the action bar.
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.tab_title_stores));
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }







    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {

        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_image_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_save_home) {
            Intent intent = new Intent(SaveShareImageActivity.this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        } else if (id == R.id.action_rate) {
            rate();
        } else if (id == R.id.action_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getApplicationInfo().loadLabel(getPackageManager()).toString());
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommand_message) + "  https://play.google.com/store/apps/details?id=" + getPackageName()+ " \n");
                startActivity(Intent.createChooser(i, "Choose one"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.action_more) {
            Intent inMoreapp = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id="+getString(R.string.moreappaccount)));
            if (isAvailable(inMoreapp)) {
                startActivity(inMoreapp);
            } else {
                Toast.makeText(SaveShareImageActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    void rate() {
        Intent intentRateMe = new Intent(Intent.ACTION_VIEW);
        if (ImageUtility.getAmazonMarket(SaveShareImageActivity.this)) {
            intentRateMe.setData(Uri.parse("amzn://apps/android?p=" + getPackageName().toLowerCase()));
        } else {
            intentRateMe.setData(Uri.parse("market://details?id=" + getPackageName().toLowerCase()));
        }
        startActivity(intentRateMe);
    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        DisplayMetrics metrics;
        BitmapLoader bitmapLoader;

        public BitmapWorkerTask() {
            File file = new File(imagePath);
            metrics = getResources().getDisplayMetrics();
            bitmapLoader = new BitmapLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... arg0) {
            try {
                return bitmapLoader.load(getApplicationContext(), new int[]{metrics.widthPixels, metrics.heightPixels}, imagePath);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                shareImageview.setImageBitmap(bitmap);
            } else {
                Toast.makeText(SaveShareImageActivity.this, getString(R.string.error_img_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void myClickHandler(View view) {
        int id = view.getId();

        if (id == R.id.share_imageView) {
            Toast.makeText(this, getString(R.string.saved_image_message), Toast.LENGTH_SHORT).show();
        }


    }

    private void initShareIntent(String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
                share, 0);
        if (!resInfo.isEmpty()) {
            // FilePath = getImagePath();

            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type)
                        || info.activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT,
                            "Created With #Photo Editor App");

                    share.putExtra(Intent.EXTRA_TEXT,
                            "Created With #Photo Editor App");

                    share.putExtra(Intent.EXTRA_STREAM,
                            Uri.fromFile(new File(imagePath)));
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, getString(R.string.no_facebook_app), Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(Intent.createChooser(share, "Select"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
