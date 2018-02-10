package com.appsx.childrensactivitycontrol.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPHelper {

    public static boolean isServiceRunning(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_running", false);
    }

    public static void setIsServiceRunning(Context context, boolean isRunning){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_running", isRunning).commit();
    }

    public static void setIsScreenOn(Context context,boolean screenOn){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_screen_on", screenOn).commit();
    }


    public static boolean isIsScreenOn(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_screen_on", true);
    }
}
