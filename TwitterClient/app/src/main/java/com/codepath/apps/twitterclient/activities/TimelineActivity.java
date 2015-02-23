package com.codepath.apps.twitterclient.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetsArrayAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {
    private static final int COMPOSE_TWEET = 111;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        lvTweets.setOnScrollListener(new EndlessScrollListener(20) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TwitterApplication.TAG, "load more tweets");
                populateTimeline();
            }
        });

        populateTimeline();
    }

    private void handleFailure(JSONObject errorResponse) {
        Log.e(TwitterApplication.TAG, errorResponse.toString());

        try {
            JSONArray errors = errorResponse.getJSONArray("errors");

            for (int i = 0; i < errors.length(); ++i) {
                JSONObject error = errors.getJSONObject(i);
                int errorCode = error.getInt("code");
                String message = error.getString("message");

                Log.e(TwitterApplication.TAG, "Error (" + errorCode + "): " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshTimeline() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        long sinceId = (!aTweets.isEmpty()) ? aTweets.getItem(0).getUid() : 1;

        client.refreshTimeline(sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(response);
                newTweets.addAll(tweets);

                aTweets.clear();
                aTweets.addAll(newTweets);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                handleFailure(errorResponse);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    // Sends an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    private void populateTimeline() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        long maxId = 0;
        if (!tweets.isEmpty()) {
            Tweet tweet = tweets.get(tweets.size() - 1);
            maxId = tweet.getUid();
        }

        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJSONArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                handleFailure(errorResponse);
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.compose_tweet) {
            Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(intent, COMPOSE_TWEET);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == COMPOSE_TWEET) {
            Bundle extras = data.getExtras();
            Tweet tweet = (Tweet) extras.getSerializable("tweet");
            aTweets.insert(tweet, 0);
        }
    }
}
