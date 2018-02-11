package com.appsx.childrensactivitycontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * This class contains the data and representations of the data structure stored in the data world.
 * This class only uses BaseDataMaster.class
 */
public class BaseDataHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chac_event"; // App database name
    public static final int DB_VERSION = 1; // version of database

    /**
     * The class describing the most important table in the database.
     * This table stores all application-related data.
     */
    public static class Event implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String EVENT_NAME = "app_name"; // name of running app
        public static final String EVENT_TIME_START = "time_start"; // contain date when user is run app
        public static final String EVENT_TIME_END = "time_end"; // contain date when app stop working
        public static final String EVENT_PACKAGE = "app_package"; // app package of running app

    }

    public static class Update implements BaseColumns {
        public static final String TABLE_NAME = "update_date";
        public static final String LAST_LOCAL_DATA_UPDATE_TIME = "time";
    }

    // Scripts to create and work whits tables
    static String SCRIPT_CREATE_TBL_EVENT = " CREATE TABLE " +
            Event.TABLE_NAME + " ( " +
            Event._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Event.EVENT_NAME + " TEXT, " +
            Event.EVENT_PACKAGE + " TEXT, " +
            Event.EVENT_TIME_START + " TEXT, " +
            Event.EVENT_TIME_END + " TEXT );";

    static String SCRIPT_CREATE_TBL_UPDATE_DATE = " CREATE TABLE " +
            Update.TABLE_NAME + " ( " +
            Update._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Update.LAST_LOCAL_DATA_UPDATE_TIME + " TEXT );";


    public BaseDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(SCRIPT_CREATE_TBL_EVENT);
        db.execSQL(SCRIPT_CREATE_TBL_UPDATE_DATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + Event.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE " + Update.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
