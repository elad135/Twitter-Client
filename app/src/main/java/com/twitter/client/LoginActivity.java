package com.twitter.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.twitter.client.utils.PrefUtils;
import com.twitter.client.utils.TwitterUtils;

import java.sql.Time;

import twitter4j.auth.RequestToken;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());

        if (sharedPreferences.getBoolean(PrefUtils.PREFERENCE_TWITTER_IS_LOGGED_IN,false)) {
            // If we're already logged in - go to TimelineActivity
            Intent timelineIntent = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(timelineIntent);
            finish();
        } else {
            // Not logged in - allow user to login
            Button buttonTwitterLogin = (Button) findViewById(R.id.twitter_login_btn);
            buttonTwitterLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TwitterAuthenticateTask().execute();
                }
            });
        }

    }

    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtils.getInstance(LoginActivity.this).getRequestToken();
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
            startActivity(intent);
            finish();
        }

    }

}
