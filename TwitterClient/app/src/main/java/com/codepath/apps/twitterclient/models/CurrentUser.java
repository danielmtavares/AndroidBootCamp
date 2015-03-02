package com.codepath.apps.twitterclient.models;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

// See the link below for reference on the information available
// https://dev.twitter.com/rest/reference/get/account/verify_credentials
public class CurrentUser {
    private static long id;
    private static String screenName;
    private static String name;
    private static String profileImageUrl;

    public static long getId() {
        return id;
    }

    public static String getScreenName() {
        return screenName;
    }

    public static String getName() {
        return name;
    }

    public static String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static void verifyCredentials(Context context) {
        if (!TwitterApplication.isNetworkAvailable(context)) {
            return;
        }

        TwitterClient client = TwitterApplication.getRestClient();
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    CurrentUser.id = response.getLong("id");
                    CurrentUser.screenName = response.getString("screen_name");
                    CurrentUser.name = response.getString("name");
                    CurrentUser.profileImageUrl = response.getString("profile_image_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.e(TwitterApplication.TAG, errorResponse.toString());
            }
        });

    }
}
