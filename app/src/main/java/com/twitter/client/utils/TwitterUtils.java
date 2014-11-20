package com.twitter.client.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.twitter.client.Constants;
import com.twitter.client.StatusData;

import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Elad on 18/11/2014.
 */
public class TwitterUtils {
    private static final String TAG = "TwitterUtils";
    public static String TWITTER_CALLBACK_URL = "oauth://com.twitter.client_oAuth";
    public static String TWITTER_CONSUMER_KEY = "WmKLPyaj4zWXf49h2uMoGaRhz";
    public static String TWITTER_CONSUMER_SECRET = "pn2GP65lLiIzsQhHtie88HTT4bObcu2eTxbWOVzt8d17GLOFPt";

    private RequestToken requestToken = null;
    private TwitterFactory twitterFactory = null;
    private Twitter twitter;
    private StatusData statusData;

    private static TwitterUtils instance;

    public static TwitterUtils getInstance(Context context) {
        if (instance == null) {
            instance = new TwitterUtils(context);
        }
        return instance;
    }

    public TwitterUtils(Context context) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();
        statusData = new StatusData(context);
    }

    public TwitterFactory getTwitterFactory()
    {
        return twitterFactory;
    }

    public void setTwitterFactory(AccessToken accessToken)
    {
        twitter = twitterFactory.getInstance(accessToken);
    }

    public Twitter getTwitter()
    {
        return twitter;
    }

    public RequestToken getRequestToken() {
        if (requestToken == null) {
            try {
                requestToken = twitterFactory.getInstance().getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return requestToken;
    }

    public StatusData getStatusData() {
        return statusData;
    }

    public void fetchStatusUpdates(double latitude, double longtitude) {
        Twitter twitter = TwitterUtils.this.getTwitter();

        try {
            // Get my location
            Location myLocation = getLocation(latitude, longtitude);

            // Get timeline statuses and insert into DB
            List<twitter4j.Status> statusUpdates = twitter.getHomeTimeline();
            if (statusUpdates != null && statusUpdates.size() > 0) {
                getStatusData().clearData();
            }
            ContentValues values = new ContentValues();
            for (twitter4j.Status status : statusUpdates) {
                // Generate Hashtags string
                String hashtags = (status.getHashtagEntities() != null) ?
                        generateHashtagsString(status.getHashtagEntities()) :
                        "";
                // Get status location
                Location statusLocation;
                if (status.getGeoLocation() != null) {
                    statusLocation = getLocation(status.getGeoLocation().getLatitude(),
                            status.getGeoLocation().getLongitude());
                    Log.d(TAG, "My Lat: " + myLocation.getLatitude() + ", my Long: " + myLocation.getLongitude() +
                            "\nStatus Lat: " + status.getGeoLocation().getLatitude() + ", Status Long: " + status.getGeoLocation().getLongitude());
                } else {
                    statusLocation = getLocation(1000, 1000); // Far far away
                }

                // Get Distance
                int distance = Math.round(myLocation.distanceTo(statusLocation));

                // Set values to be inserted to the DB
                values.put(StatusData.C_ID, status.getId());
                values.put(StatusData.C_TEXT, status.getText());
                values.put(StatusData.C_HASHTAGS, hashtags);
                values.put(StatusData.C_LOCATION, status.getPlace() != null ?
                        status.getPlace().getFullName() :
                        "Unavailable");
                values.put(StatusData.C_DISTANCE, distance);
                values.put(StatusData.C_PROFILE_PIC, status.getUser().getProfileImageURL());
                Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
                // Insert data
                getStatusData().insertOrIgnore(values);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch status updates", e);
        }

    }

    private Location getLocation(double latitude, double longtitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longtitude);
        return location;
    }

    private String generateHashtagsString(HashtagEntity[] hashtags) {
        String hashtagsString = "";
        for (HashtagEntity hashtag : hashtags) {
            hashtagsString += String.format("#%s ", hashtag.getText());
        }
        return hashtagsString;
    }
}
