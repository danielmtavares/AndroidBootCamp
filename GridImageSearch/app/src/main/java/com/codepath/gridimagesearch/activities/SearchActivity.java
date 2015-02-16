package com.codepath.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.gridimagesearch.helpers.EndlessScrollListener;
import com.codepath.gridimagesearch.helpers.SearchFilter;
import com.codepath.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    private static final int REQUEST_FILTERS = 111;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private String imageSizeFilter;
    private String colorFilter;
    private String imageTypeFilter;
    private String siteFilter;
    private JSONObject cursor;
    private String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResults = new ArrayList<>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);

        SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        // Read filter settings from preferences
        imageSizeFilter = preferences.getString(SearchFilter.IMAGE_SIZE, "");
        colorFilter = preferences.getString(SearchFilter.COLOR, "");
        imageTypeFilter = preferences.getString(SearchFilter.IMAGE_TYPE, "");
        siteFilter = preferences.getString(SearchFilter.SITE, "");
    }

    private void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult imageResult = imageResults.get(position);
                i.putExtra("result", imageResult);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreImages(page - 1);
            }
        });
    }

    // Append more data into the adapter
    private void loadMoreImages(int page) {
        if (cursor == null) {
            return;
        }
        try {
            JSONArray pages = cursor.getJSONArray("pages");
            if (page < pages.length()) {
                String startPage = pages.getJSONObject(page).getString("start");
                performSearch(startPage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILTERS) {
            Bundle extras = data.getExtras();
            SharedPreferences.Editor preferenceEditor = getSharedPreferences(getPackageName(),
                    Context.MODE_PRIVATE).edit();

            imageSizeFilter = extras.getString(SearchFilter.IMAGE_SIZE);
            preferenceEditor.putString(SearchFilter.IMAGE_SIZE, imageSizeFilter);

            colorFilter = extras.getString(SearchFilter.COLOR);
            preferenceEditor.putString(SearchFilter.COLOR, colorFilter);

            imageTypeFilter = extras.getString(SearchFilter.IMAGE_TYPE);
            preferenceEditor.putString(SearchFilter.IMAGE_TYPE, imageTypeFilter);

            siteFilter = extras.getString(SearchFilter.SITE);
            preferenceEditor.putString(SearchFilter.SITE, siteFilter);

            preferenceEditor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                imageResults.clear();
                aImageResults.notifyDataSetChanged();
                queryString = query;
                performSearch("0");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem preferencesItem = menu.findItem(R.id.action_preferences);
        preferencesItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(SearchActivity.this, AdvancedFiltersActivity.class);
                intent.putExtra(SearchFilter.IMAGE_SIZE, imageSizeFilter);
                intent.putExtra(SearchFilter.COLOR, colorFilter);
                intent.putExtra(SearchFilter.IMAGE_TYPE, imageTypeFilter);
                intent.putExtra(SearchFilter.SITE, siteFilter);

                startActivityForResult(intent, REQUEST_FILTERS);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void performSearch(String startPage) {
        if (queryString.isEmpty()) {
            return;
        }

        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0" +
                "&q=" + queryString + "&rsz=8";

        if (!imageSizeFilter.isEmpty()) {
            searchUrl += "&imgsz=" + imageSizeFilter;
        }

        if (!colorFilter.isEmpty()) {
            searchUrl += "&imgcolor=" + colorFilter;
        }

        if (!imageTypeFilter.isEmpty()) {
            searchUrl += "&imgtype=" + imageTypeFilter;
        }

        if (!siteFilter.isEmpty()) {
            searchUrl += "&as_sitesearch=" + siteFilter;
        }

        searchUrl += "&start=" + startPage;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(searchUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject responseData = response.getJSONObject("responseData");
                    cursor = responseData.getJSONObject("cursor");

                    JSONArray imageResultsJson = responseData.getJSONArray("results");
                    imageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                aImageResults.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("ERROR", "Failed to retrieve image list. Response: " + responseString);
            }
        });
    }
}
