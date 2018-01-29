package com.appsx.childrensactivitycontrol.util.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appsx.childrensactivitycontrol.database.BaseDataMaster;
import com.appsx.childrensactivitycontrol.util.GlobalNames;

public class ScreenBroadCastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ScreenBroadCastReceiver";

    private BaseDataMaster dataMaster;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dataMaster == null) {
            dataMaster = BaseDataMaster.getDataMaster(context);
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            dataMaster.insertEvent(GlobalNames.END_LAST_APP, String.valueOf(System.currentTimeMillis()));
            Log.d(LOG_TAG, "Screen went OFF");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i(LOG_TAG, "Screen went ON");
        }
    }
}
