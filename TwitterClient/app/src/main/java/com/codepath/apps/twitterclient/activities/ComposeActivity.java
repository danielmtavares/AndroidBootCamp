package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.TwitterClient;
import com.codepath.apps.twitterclient.models.CurrentUser;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);

        Picasso picasso = Picasso.with(getApplicationContext());
        picasso.load(CurrentUser.getProfileImageUrl()).into(ivProfileImage);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(CurrentUser.getName());

        TextView tvUserName = (TextView) findViewById(R.id.tvScreenName);
        tvUserName.setText(CurrentUser.getScreenName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweet) {
            postStatusUpdate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postStatusUpdate() {
        EditText edStatus = (EditText) findViewById(R.id.edStatus);
        String message = edStatus.getText().toString();

        if (message.isEmpty()) {
            return;
        }

        TwitterClient client = TwitterApplication.getRestClient();
        client.postStatusUpdate(message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Status update posted!",
                        Toast.LENGTH_SHORT).show();
                Tweet tweet = Tweet.fromJSON(response);

                Intent data = new Intent();
                data.putExtra("tweet", tweet);

                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.e(TwitterApplication.TAG, errorResponse.toString());
                Toast.makeText(getApplicationContext(), "Failed to post status update",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
