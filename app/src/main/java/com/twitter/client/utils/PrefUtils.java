package com.twitter.client.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Elad on 20/11/2014.
 */
public class PrefUtils {

    public static String PREFERENCE_TWITTER_OAUTH_TOKEN = "TWITTER_OAUTH_TOKEN";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET = "TWITTER_OAUTH_TOKEN_SECRET";
    public static String PREFERENCE_TWITTER_IS_LOGGED_IN = "TWITTER_IS_LOGGED_IN";
    public static String PREFERENCE_TWITTER_LOAD_IN_BACKGROUND = "TWITTER_LOAD_IN_BACKGROUND";
    public static String PREFERENCE_LATITUDE = "PREFERENCE_LATITUDE";
    public static String PREFERENCE_LONGTITUDE = "PREFERENCE_LONGTITUDE";


    public static void init(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().commit();
    }

    public static String getTwitterOauthToken(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREFERENCE_TWITTER_OAUTH_TOKEN, null);
    }

    public static void setTwitterOauthToken(final Context context, final String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREFERENCE_TWITTER_OAUTH_TOKEN, token).commit();
    }

    public static String getTwitterOauthTokenSecret(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, null);
    }

    public static void setTwitterOauthTokenSecret(final Context context, final String secret) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, secret).commit();
    }

    public static String getIsLoggedIn(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREFERENCE_TWITTER_IS_LOGGED_IN, null);
    }

    public static void setIsLoggedIn(final Context context, final String loggedIn) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREFERENCE_TWITTER_IS_LOGGED_IN, loggedIn).commit();
    }

    public static boolean getUpdateLocation(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREFERENCE_TWITTER_LOAD_IN_BACKGROUND, true);
    }

    public static void setUpdateLocation(final Context context, final boolean update) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREFERENCE_TWITTER_LOAD_IN_BACKGROUND, update).commit();
    }

    public static float getLongtitude(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(PREFERENCE_LONGTITUDE, 0);
    }

    public static void setLongtitude(final Context context, final float longtitude) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putFloat(PREFERENCE_LONGTITUDE, longtitude).commit();
    }

    public static float getLatitude(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(PREFERENCE_LATITUDE, 0);
    }

    public static void setLatitude(final Context context, final float latitude) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putFloat(PREFERENCE_LATITUDE, latitude).commit();
    }
}
