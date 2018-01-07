package com.appsx.childrensactivitycontrol.app_name_util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appsx.childrensactivitycontrol.database.SPHelper;

public class AutoStrartWorker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SPHelper.isServiceRunning(context)){
            context.startService(new Intent(context,WatchingService.class));
        }
    }
}
