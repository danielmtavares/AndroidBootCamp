package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScrollListener(new EndlessScrollListener(20) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });

        setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });

        populateTimeline();
    }

    private void refreshTimeline() {
        if (!TwitterApplication.isNetworkAvailable(getActivity())) {
            if (failureListener != null) {
                failureListener.onFailure("Network is not available");
            }
            return;
        }

        long sinceId = (!aTweets.isEmpty()) ? aTweets.getItem(0).getUid() : 1;

        client.refreshHomeTimeline(sinceId, new JsonHttpResponseHandler() {
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
        if (!TwitterApplication.isNetworkAvailable(getActivity())) {
            if (failureListener != null) {
                failureListener.onFailure("Network is not available");
            }
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
}
