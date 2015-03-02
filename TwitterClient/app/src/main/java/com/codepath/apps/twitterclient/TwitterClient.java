package com.codepath.apps.twitterclient;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "w7TNjEVTd72nJnbch1oniFCjt";
    public static final String REST_CONSUMER_SECRET = "l1ppwDNT4X4N4SMd8NbE8o7T0hr9nO2DGLs5hDymzf1GbtYZb9";
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
    private static final int STATUS_LIMIT = 140;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // GET https://api.twitter.com/1.1/statuses/home_timeline.json
    // count=25
    // since_id=1
    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 20);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        params.put("exclude_replies", true);

        getClient().get(apiUrl, params, handler);
    }

    // GET https://api.twitter.com/1.1/statuses/home_timeline.json
    public void refreshHomeTimeline(long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 20);
        params.put("since_id", sinceId);
        params.put("exclude_replies", true);

        getClient().get(apiUrl, params, handler);
    }

    // Updates the authenticating user’s current status, also known as tweeting.
    //
    // POST https://api.twitter.com/1.1/statuses/update.json
    // status=<message_to_be_sent>
    public void postStatusUpdate(String status, AsyncHttpResponseHandler handler) {
        if (status.length() > STATUS_LIMIT) {
            Log.e(TwitterApplication.TAG, "Failed to post update. Exceeded status limit of " +
                    TwitterClient.STATUS_LIMIT + " characters");
            return;
        }

        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);

        getClient().post(apiUrl, params, handler);
    }

    public void verifyCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status", "true");

        getClient().get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);

        getClient().get(apiUrl, params, handler);
    }

    public void refreshMentionsTimeline(long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 20);
        params.put("since_id", sinceId);
        params.put("exclude_replies", true);

        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);

        getClient().get(apiUrl, params, handler);
    }

    public void refreshUserTimeline(long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 20);
        params.put("since_id", sinceId);
        params.put("exclude_replies", true);

        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);

        getClient().get(apiUrl, params, handler);
    }
}