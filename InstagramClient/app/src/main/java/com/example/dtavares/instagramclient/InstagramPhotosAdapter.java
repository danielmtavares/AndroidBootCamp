package com.example.dtavares.instagramclient;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    // What data do we need from the activity
    // Context, Data Source
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    // What our item looks like
    // Use the template to display each photo
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        InstagramPhoto photo = getItem(position);

        // Check if we are using a recycled view, if not we need to inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        // Lookup the views for populating the data (image, caption)
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        TextView tvLikesCount = (TextView) convertView.findViewById(R.id.tvLikesCount);
        ImageView ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePicture);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);

        // Insert the model data into each of the view items
        tvUsername.setText(photo.username);
        tvCaption.setText(photo.caption);
        tvLikesCount.setText(Integer.toString(photo.likesCount));

        CharSequence relativeTimestamp = DateUtils.getRelativeTimeSpanString(photo.createdTime * 1000,
                System.currentTimeMillis(), 0);
        tvTimestamp.setText(relativeTimestamp);

        ivProfilePicture.setImageResource(0);
        RequestCreator profilePicture = Picasso.with(getContext()).load(photo.profilePicture);
        profilePicture.transform(new CircleTransform()).into(ivProfilePicture);

        ivPhoto.setImageResource(0);
        RequestCreator photoCreator = Picasso.with(getContext()).load(photo.imageUrl);
        photoCreator.fit().centerCrop().placeholder(R.drawable.ic_launcher).into(ivPhoto);

        return convertView;
    }
}
