package com.twitter.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.twitter.client.service.UpdateService;
import com.twitter.client.utils.PrefUtils;

/**
 * Created by Elad on 19/11/2014.
 */
public class LocationReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "In LocationReceiver");

        Bundle bundle = intent.getExtras();
        Location location = (Location) bundle.get(android.location.LocationManager.KEY_LOCATION_CHANGED);

        if (location != null && PrefUtils.getUpdateLocation(context)) {
            Log.d(TAG, "Curr Lat: " + location.getLatitude() + ", curr Long: " + location.getLongitude());
            // Update SharedPrefs with current coords
            PrefUtils.setLatitude(context, (float) location.getLatitude());
            PrefUtils.setLongtitude(context, (float) location.getLongitude());
            // pull new statuses and update DB
            Intent updateIntent = new Intent(context, UpdateService.class);
            updateIntent.putExtra("lat", location.getLatitude());
            updateIntent.putExtra("long", location.getLongitude());
            context.startService(updateIntent);
        } else {
            Log.d(TAG, "No location");
        }
    }
}
