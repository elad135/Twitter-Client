package com.twitter.client.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.client.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * Created by Elad on 19/11/2014.
 */
public class StatusAdapter  extends ArrayAdapter<Status> {
    private static final String TAG = "StatusAdapter";
    private static final String DEFAULT_LOCATION = "Unavailable";

    private Context context;
    private int layoutResourceId;
    private ArrayList<Status> data = null;

    static class ViewHolder {
        CircleImageView profileImage;
        TextView txtStatusText;
        TextView txtStatusLocation;
        TextView txtStatusHashtags;
    }

    public StatusAdapter(Context context, int layoutResourceId, ArrayList<Status> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            LayoutInflater mLayoutInflater = ((Activity) context).getLayoutInflater();
            convertView = mLayoutInflater.inflate(layoutResourceId, parent, false);
            vh = new ViewHolder();
            vh.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);
            vh.txtStatusText = (TextView) convertView.findViewById(R.id.status_text);
            vh.txtStatusLocation = (TextView) convertView.findViewById(R.id.status_location);
            vh.txtStatusHashtags = (TextView) convertView.findViewById(R.id.status_hashtags);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Populate the views
        Picasso.with(context).load(data.get(position).getUser().getProfileImageURL()).into(vh.profileImage);
        vh.txtStatusText.setText(data.get(position).getText());
        vh.txtStatusLocation.setText((data.get(position).getPlace() != null) ?
                                      data.get(position).getPlace().getFullName() :
                                      DEFAULT_LOCATION);
        String hashtags = "";
        if (data.get(position).getHashtagEntities() != null) {
            for (HashtagEntity hashtag : data.get(position).getHashtagEntities()) {
                hashtags += String.format("#%s ", hashtag.getText());
            }
        }
        vh.txtStatusHashtags.setText(hashtags);

        return convertView;
    }
}
