package com.twitter.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Elad on 19/11/2014.
 */
public class StatusData {
    private static final String TAG = StatusData.class.getSimpleName();

    static final int VERSION = 2;
    static final String DATABASE = "timeline.db";
    static final String TABLE = "timeline";

    public static final String C_ID = "_id";
    public static final String C_TEXT = "txt";
    public static final String C_HASHTAGS = "hashtags";
    public static final String C_LOCATION = "location";
    public static final String C_DISTANCE = "distance";
    public static final String C_PROFILE_PIC = "profile_pic";

    private static final String GET_ALL_ORDER_BY = C_DISTANCE + " ASC";

    // DbHelper implementations
    class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Creating database: " + DATABASE);
            db.execSQL("create table " + TABLE + " (" + C_ID + " int primary key, "
                    + C_TEXT + " text, " + C_HASHTAGS + " text, " + C_LOCATION + " text, "
                    + C_DISTANCE + " int, " + C_PROFILE_PIC + " text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table " + TABLE);
            this.onCreate(db);
        }
    }

    final DbHelper dbHelper;

    public StatusData(Context context) {
        this.dbHelper = new DbHelper(context);
        Log.i(TAG, "Initialized data");
    }

    public void close() {
        this.dbHelper.close();
    }

    public void insertOrIgnore(ContentValues values) {
        Log.d(TAG, "insertOrIgnore on " + values);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        try {
            db.insertWithOnConflict(TABLE, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        } finally {
            db.close();
        }
    }

    /**
     *
     * @return Cursor where the columns are going to be id, created_at, user, txt
     */
    public Cursor getStatusUpdates() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
    }

    /**
     * Deletes all the data
     */
    public void clearData() {
        // Open Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the data
        db.delete(TABLE, null, null);

        // Close Database
        db.close();
    }
}