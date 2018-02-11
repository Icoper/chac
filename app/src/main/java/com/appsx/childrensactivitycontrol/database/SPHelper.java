package com.appsx.childrensactivitycontrol.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPHelper {

    public static String getPassLogin(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("pass_login", "");
    }

    public static void setPassLogin(Context context, String pass) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("pass_login", pass).apply();
    }

    public static boolean isAutoStartPermGranted(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_auto_start_perm_granted", false);
    }

    public static void setisAutoStartPermGranted(Context context, boolean isGranted) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_auto_start_perm_granted", isGranted).apply();
    }

    public static boolean isServiceRunning(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_running", false);
    }

    public static void setIsServiceRunning(Context context, boolean isRunning){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_running", isRunning).apply();
    }

    public static void setIsScreenOn(Context context,boolean screenOn){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_screen_on", screenOn).apply();
    }


    public static boolean isIsScreenOn(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_screen_on", true);
    }
}
