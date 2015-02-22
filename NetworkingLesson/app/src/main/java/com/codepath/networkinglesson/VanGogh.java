package com.codepath.networkinglesson;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class VanGogh {
    public static VanGogh vanGogh;

    public static void loadImage(String urlString, ImageView imageView) {
        if (vanGogh == null) {
            vanGogh = new VanGogh();
        }

        vanGogh.loadPrivate(urlString, imageView);
    }

    private void loadPrivate(String urlString, ImageView imageView) {
        new ImageLoader(imageView).execute(urlString);
    }

    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.connect();
                InputStream in = conn.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.imageView.setImageBitmap(bitmap);
        }

        public ImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
