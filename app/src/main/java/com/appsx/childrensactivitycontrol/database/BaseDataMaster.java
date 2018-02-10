package com.appsx.childrensactivitycontrol.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appsx.childrensactivitycontrol.model.AppListModel;
import com.appsx.childrensactivitycontrol.util.GlobalNames;

import java.util.ArrayList;

public class BaseDataMaster {
    private static final String LOG_TAG = "BaseDataMaster";
    private SQLiteDatabase database;
    private BaseDataHelper dbCreator;
    private Context context;

    private static BaseDataMaster dataMaster;

    private BaseDataMaster(Context context) {
        this.context = context;
        dbCreator = new BaseDataHelper(context);
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
    }

    public static BaseDataMaster getDataMaster(Context context) {
        if (dataMaster == null) {
            dataMaster = new BaseDataMaster(context);
        }
        return dataMaster;
    }

    /**
     * Add new event on data base or rewrite last event
     */
    public synchronized void insertEvent(String appPackage, String date) {

        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }

        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        String appName = "";
        try {
            if (!appPackage.equals(GlobalNames.END_LAST_APP)) {
                appName = (String) packageManager
                        .getApplicationLabel(packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!appName.isEmpty() && !appName.equals(GlobalNames.END_LAST_APP) && SPHelper.isIsScreenOn(context)) {

            String query = "SELECT * FROM " + BaseDataHelper.Event.TABLE_NAME;
            Cursor cursor = database.rawQuery(query, null);
            String eventId = "";

            if (!cursor.isAfterLast()) {
                cursor.moveToLast();
                String lastEventDataEnd = "";
                try {
                    eventId = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event._ID));
                    lastEventDataEnd = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_END));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (lastEventDataEnd == null) {
                    // Обновляем последнюю запись
                    Log.d(LOG_TAG, "insertEvent - update last");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BaseDataHelper.Event.EVENT_TIME_END, date);
                    database.update(BaseDataHelper.Event.TABLE_NAME, contentValues, "_id = ?",
                            new String[]{eventId});
                    // Создаем новою запись
                    Log.d(LOG_TAG, "insertEvent - create new : " + appPackage);
                    ContentValues _contentValues = new ContentValues();
                    _contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
                    _contentValues.put(BaseDataHelper.Event.EVENT_PACKAGE, appPackage);
                    _contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
                    database.insert(BaseDataHelper.Event.TABLE_NAME, null, _contentValues);

                } else {
                    // Создаем новою запись
                    Log.d(LOG_TAG, "insertEvent - create new : " + appPackage);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
                    contentValues.put(BaseDataHelper.Event.EVENT_PACKAGE, appPackage);
                    contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
                    database.insert(BaseDataHelper.Event.TABLE_NAME, null, contentValues);
                }


            } else {
                Log.d(LOG_TAG, "insertEvent - create first point");
                ContentValues contentValues = new ContentValues();
                contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
                contentValues.put(BaseDataHelper.Event.EVENT_PACKAGE, appPackage);
                contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
                database.insert(BaseDataHelper.Event.TABLE_NAME, null, contentValues);
                cursor.close();
            }
        } else if (appPackage.equals(GlobalNames.END_LAST_APP)) {
            String query = "SELECT * FROM " + BaseDataHelper.Event.TABLE_NAME;
            Cursor cursor = database.rawQuery(query, null);
            String eventId = "";

            if (!cursor.isAfterLast()) {
                cursor.moveToLast();
                String lastEventDataEnd = "";
                try {
                    eventId = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event._ID));
                    lastEventDataEnd = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_END));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (lastEventDataEnd == null) {
                    Log.d(LOG_TAG, "insertEvent - update last(screen off)");
                    // Обновляем последнюю запись
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BaseDataHelper.Event.EVENT_TIME_END, date);
                    database.update(BaseDataHelper.Event.TABLE_NAME, contentValues, "_id = ?",
                            new String[]{eventId});

                }
            }
            cursor.close();
        }
        dbCreator.close();

    }

    public void repeatLastEvent(String date) {
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }

        String query = "SELECT * FROM " + BaseDataHelper.Event.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        String eventId = "";

        cursor.moveToLast();
        String lastEventName = "";
        String lastEventPackage = "";
        try {
            eventId = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event._ID));
            lastEventName = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_NAME));
            lastEventPackage = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_PACKAGE));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Создаем новою запись
        Log.d(LOG_TAG, "insertEvent - create new : " + lastEventPackage);
        ContentValues _contentValues = new ContentValues();
        _contentValues.put(BaseDataHelper.Event.EVENT_NAME, lastEventName);
        _contentValues.put(BaseDataHelper.Event.EVENT_PACKAGE, lastEventPackage);
        _contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
        database.insert(BaseDataHelper.Event.TABLE_NAME, null, _contentValues);


    }

    public ArrayList<AppListModel> getEvents() {
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
        String query = "SELECT  * FROM " +
                BaseDataHelper.Event.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<AppListModel> list = new ArrayList<>();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                AppListModel app = new AppListModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_PACKAGE)),
                        "",
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_END))
                );
                list.add(app);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbCreator.close();
        cursor.close();
        Log.d(LOG_TAG, "return event = " + list.size());

        return list;
    }

    public void insertScreenState(String time) {

        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
        String query = "SELECT * FROM " + BaseDataHelper.ScreenWork.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        String eventId = "";
        if (!cursor.isAfterLast()) {
            cursor.moveToLast();
            String lastTimeScreenOff = "";
            try {
                eventId = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.ScreenWork._ID));
                lastTimeScreenOff = cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.ScreenWork.SCREEN_OFF));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (lastTimeScreenOff == null) {
                // Обновляем последнюю запись
                ContentValues contentValues = new ContentValues();
                contentValues.put(BaseDataHelper.ScreenWork.SCREEN_OFF, time);
                database.update(BaseDataHelper.ScreenWork.TABLE_NAME, contentValues, "_id = ?",
                        new String[]{eventId});

            } else {
                // Создаем новою запись
                ContentValues contentValues = new ContentValues();
                contentValues.put(BaseDataHelper.ScreenWork.SCREEN_ON, time);
                database.insert(BaseDataHelper.ScreenWork.TABLE_NAME, null, contentValues);
            }


        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BaseDataHelper.ScreenWork.SCREEN_ON, time);
            database.insert(BaseDataHelper.ScreenWork.TABLE_NAME, null, contentValues);
        }
        cursor.close();
        dbCreator.close();
    }
}