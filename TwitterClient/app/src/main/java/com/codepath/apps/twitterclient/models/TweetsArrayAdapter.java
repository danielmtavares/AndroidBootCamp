package com.codepath.apps.twitterclient.models;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

// Taking the Tweet objects and turning them into Views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    final static String TWITTER_TIMESTAMP_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the tweet
        Tweet tweet = getItem(position);

        // Find or inflate the template
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);

            // Find the subviews to fill with data in the template
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the subviews
        User user = tweet.getUser();

        viewHolder.tvName.setText(user.getName());
        viewHolder.tvUserName.setText("@" + user.getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(viewHolder.ivProfileImage);

        // Return the view to be inserted into the list
        return convertView;
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_TIMESTAMP_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvName;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTimestamp;
    }
}
