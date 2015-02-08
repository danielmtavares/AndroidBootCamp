package com.example.dtavares.instagramclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    private static final String CLIENT_ID = "c3cb7decba8648eea775834ff2edcd22";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photos = new ArrayList<>();
        setContentView(R.layout.activity_photos);

        aPhotos = new InstagramPhotosAdapter(this, photos);
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);

        // fetch the popular photos
        fetchPopularPhotos();
    }

    private void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /*
                Expecting a JSON object
                - Type: { "data" => [x] => "type" } ("image" or "video")
                - URL: { "data" => [x] => "images" => "standard_resolution" => "url" }
                - Caption: { "data" => [x] => "caption" => "text" }
                - Author name: { "data" => [x] => "user" => "username" }
                 */
                // decode the JSON attributes into the data model
                try {
                    JSONArray photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); ++i) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        InstagramPhoto photo = new InstagramPhoto();

                        JSONObject userObject = photoJSON.getJSONObject("user");
                        photo.username = userObject.getString("username");
                        photo.profilePicture = userObject.getString("profile_picture");
                        photo.createdTime = photoJSON.getInt("created_time");
                        photo.caption = photoJSON.getJSONObject("caption").getString("text");

                        JSONObject imagesObject = photoJSON.getJSONObject("images");
                        JSONObject standardResolutionObject = imagesObject.getJSONObject("standard_resolution");

                        photo.imageUrl = standardResolutionObject.getString("url");
                        photo.imageHeight = standardResolutionObject.getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // callback
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("DEBUG", "Failed to retrieve list of popular photos. Response: " + responseString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
