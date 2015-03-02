package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.TweetsArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TweetsListFragment extends Fragment {
    public ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected TwitterClient client;
    protected OnFailureListener failureListener;
    private ListView lvTweets;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private EndlessScrollListener scrollListener;

    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void setScrollListener(EndlessScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(refreshListener);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets = (ListView) view.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(scrollListener);

        return view;
    }

    // creation lifecycle event

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public void insert(Tweet tweet, int index) {
        aTweets.insert(tweet, index);
    }

    protected void handleFailure(JSONObject errorResponse) {
        Log.e(TwitterApplication.TAG, errorResponse.toString());

        try {
            JSONArray errors = errorResponse.getJSONArray("errors");

            for (int i = 0; i < errors.length(); ++i) {
                JSONObject error = errors.getJSONObject(i);
                int errorCode = error.getInt("code");
                String message = error.getString("message");

                Log.e(TwitterApplication.TAG, "Error (" + errorCode + "): " + message);
                if (failureListener != null) {
                    failureListener.onFailure(message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnFailureListener {
        public void onFailure(String errorMessage);
    }
}
