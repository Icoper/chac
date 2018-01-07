package com.appsx.childrensactivitycontrol.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dmitriysamoilov on 04.01.18.
 */

public class SPHelper {

    public static boolean isServiceRunning(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_running", true);
    }

    public static void setIsServiceRunning(Context context, boolean isRunning){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_running", isRunning).commit();
    }
}
