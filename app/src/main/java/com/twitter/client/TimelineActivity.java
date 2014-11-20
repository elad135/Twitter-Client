package com.twitter.client;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.client.utils.PrefUtils;
import com.twitter.client.utils.TwitterUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class TimelineActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";

    private String GET_LOCATION_CHANGE_MESSAGE = "com.twitter.client.LOCATION_READY";
    private int UPDATE_LOCATION_MIN_TIME = 120000; // 2 min
    private int UPDATE_LOCATION_MIN_DISTANCE = 1000; // 1 km
    static final String[] FROM = { StatusData.C_TEXT, StatusData.C_HASHTAGS, StatusData.C_LOCATION,
            StatusData.C_PROFILE_PIC };
    static final int[] TO = { R.id.status_text, R.id.status_hashtags, R.id.status_location,
            R.id.profile_image };

    // Views
    private ListView mListView;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mListView = (ListView) findViewById(R.id.status_list);

        mAdapter = new SimpleCursorAdapter(this, R.layout.status_list_item,
                TwitterUtils.getInstance(this).getStatusData().getStatusUpdates(), FROM, TO,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mAdapter.setViewBinder(VIEW_BINDER);
        mListView.setAdapter(mAdapter);

        initTwitterAuthParams();

        registerLocationChangeReceiver();

        new refreshTimeline().execute();
    }

    /**
     * Load the statuses that are currently stored in the DB
     */
    class refreshTimeline extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            TwitterUtils.getInstance(TimelineActivity.this).fetchStatusUpdates(0,0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.changeCursor(TwitterUtils.getInstance(TimelineActivity.this).getStatusData().getStatusUpdates());
            Toast.makeText(TimelineActivity.this, "Refreshed timeline", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * View binder to inject profile image
     */
    final SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if(view.getId() != R.id.profile_image) return false;
            Picasso.with(TimelineActivity.this).load(cursor.getString(columnIndex)).into((ImageView) view);
            return true;
        }
    };

    /**
     * Initialize Twitter auth params and update SharedPrefs if post login.
     * Otherwise just Initialize Twitter auth params.
     */
    private void initTwitterAuthParams() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(Constants.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            new InitParamsTask().execute(verifier);
        } else
            new InitParamsTask().execute("");
    }

    /**
     * Register receiver to be called upon on location change
     */
    private void registerLocationChangeReceiver() {
        Intent intent = new Intent(GET_LOCATION_CHANGE_MESSAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, intent, 0);


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_LOCATION_MIN_TIME,
                UPDATE_LOCATION_MIN_DISTANCE, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new refreshTimeline().execute();
            return true;
        } else if (id == R.id.action_sync) {
            PrefUtils.setUpdateLocation(this, !PrefUtils.getUpdateLocation(this));
            String message = "";
            if (PrefUtils.getUpdateLocation(this)) {
                message = "Background udpates enabled";
            } else {
                message = "Background udpates disabled";
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    class InitParamsTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {

            Twitter twitter = TwitterUtils.getInstance(TimelineActivity.this).getTwitter();
            RequestToken requestToken = TwitterUtils.getInstance(TimelineActivity.this).getRequestToken();
            if (params[0] != null && !params[0].isEmpty()) {
                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PrefUtils.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(PrefUtils.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(PrefUtils.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.putBoolean(PrefUtils.PREFERENCE_TWITTER_LOAD_IN_BACKGROUND, true);
                    editor.commit();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(PrefUtils.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(PrefUtils.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                TwitterUtils.getInstance(TimelineActivity.this).setTwitterFactory(accessToken);
            }

            return null;
        }
    }
}
