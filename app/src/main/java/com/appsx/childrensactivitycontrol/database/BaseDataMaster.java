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

/**
 * The class does all the work associated with writing or reading data from the local database.
 * Only this class implements the logic of reading / writing data to a local database.
 */
public class BaseDataMaster {
    private static final String LOG_TAG = "BaseDataMaster";

    private SQLiteDatabase database; // an instance is used for all manipulations with the database
    private BaseDataHelper dbCreator; // an instance is used for create db
    private Context context;
    private static BaseDataMaster dataMaster;


    private BaseDataMaster(Context context) {
        this.context = context;
        dbCreator = new BaseDataHelper(context);
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
    }

    /**
     * Creates a new instance or returns an already created
     */
    public static BaseDataMaster getDataMaster(Context context) {
        if (dataMaster == null) {
            dataMaster = new BaseDataMaster(context);
        }
        return dataMaster;
    }

    /**
     * Add new event on data base or rewrite last event
     * Each time a new event is recorded, we take the last entry in the table,
     * and check if the record has an end time. If the last record has an end time,
     * then we create a new record and write the date to the start date. Otherwise,
     * add the end time to the last event
     *
     * @param appPackage package name of a running or closed app
     * @param date date of event. Attention! Can contain a text constant
     *             GlobalNames.END_LAST_APP that indicates that the method was called from ScreenBroadCastReceiver.class
     *             Since the user has locked the device, and WatchingService.class could not handle this as standard.
     *             In this case, we simply update the last record in the table
     */
    public synchronized void insertEvent(String appPackage, String date) {

        // we need it to learn the name of the application
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        String appName = "";
        // Try to get the name of the application by the name of the package
        try {
            if (!appPackage.equals(GlobalNames.END_LAST_APP)) {
                appName = (String) packageManager
                        .getApplicationLabel(packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            if (!appName.isEmpty() && !appName.equals(GlobalNames.END_LAST_APP) && SPHelper.isIsScreenOn(context)) {

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
                    // table is empty, need to create new event
                    if (lastEventDataEnd == null) {
                        // update last event
                        Log.d(LOG_TAG, "insertEvent - update last");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(BaseDataHelper.Event.EVENT_TIME_END, date);
                        database.update(BaseDataHelper.Event.TABLE_NAME, contentValues, "_id = ?",
                                new String[]{eventId});
                        // create new event
                        Log.d(LOG_TAG, "insertEvent - create new : " + appPackage);
                        ContentValues _contentValues = new ContentValues();
                        _contentValues.put(BaseDataHelper.Event.EVENT_NAME, appName);
                        _contentValues.put(BaseDataHelper.Event.EVENT_PACKAGE, appPackage);
                        _contentValues.put(BaseDataHelper.Event.EVENT_TIME_START, date);
                        database.insert(BaseDataHelper.Event.TABLE_NAME, null, _contentValues);

                    } else {
                        // create new event
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

            /* hence the method was called ScreenBoardCastReceiver.class,
                the user blocked the screen, write the time of the end of the last event*/
            } else if (appPackage.equals(GlobalNames.END_LAST_APP)) {
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
                        Log.d(LOG_TAG, "insertEvent - update last(screen off)");
                        // update last event
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(BaseDataHelper.Event.EVENT_TIME_END, date);
                        database.update(BaseDataHelper.Event.TABLE_NAME, contentValues, "_id = ?",
                                new String[]{eventId});

                    }
                }
                cursor.close();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        dbCreator.close();

    }

    /**
     * Called from ScreenBroadCastReceiver.class when the user unlocked the device.
     * The method selects the last record in the table,
     * then creates the ephemeral event with the next record and writes
     *
     * @param date (date of unlocking) to the start date
     */
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

    /**
     * @return list of all db events
     */
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

}