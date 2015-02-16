package com.codepath.gridimagesearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ImageDisplayActivity extends ActionBarActivity {

    private ShareActionProvider shareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ImageResult imageResult = (ImageResult) getIntent().getSerializableExtra("result");
        ImageView ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
        Picasso.with(this).load(imageResult.fullUrl).into(ivImageResult, new Callback() {
            @Override
            public void onSuccess() {
                setupShareIntent();
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "Failed to load image",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_imagedisplay, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        return super.onCreateOptionsMenu(menu);
    }

    // Can be triggered by a view event such as a button press
    public void setupShareIntent() {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) findViewById(R.id.ivImageResult);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");

            shareAction.setShareIntent(shareIntent);
        } else {
            // ...sharing failed, handle error
            String message = "Failed to share image";
            Log.e("GIS", message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable mDrawable = imageView.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                mBitmap, "Image Description", null);

        return Uri.parse(path);
    }
}
