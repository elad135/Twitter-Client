package com.twitter.client.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.twitter.client.utils.TwitterUtils;

public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Update statuses in DB
        TwitterUtils.getInstance(this).fetchStatusUpdates(intent.getDoubleExtra("lat", 1000),
                                                          intent.getDoubleExtra("long", 1000));
    }

}
