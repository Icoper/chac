package com.appsx.childrensactivitycontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class BaseDataHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chac_event";
    public static final int DB_VERSION = 1;

    public static class Event implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String EVENT_NAME = "app_name";
        public static final String EVENT_TIME_START = "time_start";
        public static final String EVENT_TIME_END = "time_end";
        public static final String EVENT_PACKAGE = "app_package";

    }

    public static class Update implements BaseColumns {
        public static final String TABLE_NAME = "update_date";
        public static final String LAST_LOCAL_DATA_UPDATE_TIME = "time";
    }

    public static class ScreenWork implements BaseColumns {
        public static final String TABLE_NAME = "screen_work";
        public static final String SCREEN_ON = "on_screen";
        public static final String SCREEN_OFF = "off_screen";
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

    static String SCRIPT_CREATE_TBL_SCREEN_WORK = " CREATE TABLE " +
            ScreenWork.TABLE_NAME + " ( " +
            ScreenWork._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ScreenWork.SCREEN_ON + " TEXT, " +
            ScreenWork.SCREEN_OFF + " TEXT );";

    public BaseDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TBL_EVENT);
        db.execSQL(SCRIPT_CREATE_TBL_UPDATE_DATE);
        db.execSQL(SCRIPT_CREATE_TBL_SCREEN_WORK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + Event.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE " + Update.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE " + ScreenWork.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
