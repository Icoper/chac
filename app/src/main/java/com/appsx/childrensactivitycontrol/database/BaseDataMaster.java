package com.appsx.childrensactivitycontrol.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appsx.childrensactivitycontrol.model.EventModel;

import java.util.ArrayList;

public class BaseDataMaster {
    private static final String LOG_TAG = "BaseDataMaster";
    private SQLiteDatabase database;
    private BaseDataHelper dbCreator;

    private static BaseDataMaster dataMaster;

    private BaseDataMaster(Context context) {
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
    public void insertEvent(String appName, String date) {
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
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
                ContentValues contentValues = new ContentValues();
                contentValues.put(BaseDataHelper.Event.EVENT_TIME_END, date);
                database.update(BaseDataHelper.Event.TABLE_NAME, contentValues, "_id = ?",
                        new String[]{eventId});

            } else {
                // Создаем новою запись
                ContentValues contentValues = new ContentValues();
                contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
                contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
                database.insert(BaseDataHelper.Event.TABLE_NAME, null, contentValues);
            }


        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
            contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
            database.insert(BaseDataHelper.Event.TABLE_NAME, null, contentValues);
        }
        cursor.close();
        dbCreator.close();

    }

    public void deleteItem(String itemKey) {
//         database.delete(BaseDataHelper.User.TABLE_NAME, BaseDataHelper.User.MAIN_KEY + "='" + itemKey + "'", null);
    }

    public ArrayList<EventModel> getEvents() {
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
        String query = "SELECT  * FROM " +
                BaseDataHelper.Event.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<EventModel> list = new ArrayList<>();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                EventModel eventModel = new EventModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.Event.EVENT_TIME_END))
                );
                list.add(eventModel);
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
}