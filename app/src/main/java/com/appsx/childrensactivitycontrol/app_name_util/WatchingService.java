package com.appsx.childrensactivitycontrol.app_name_util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.appsx.childrensactivitycontrol.database.BaseDataHelper;
import com.appsx.childrensactivitycontrol.database.BaseDataMaster;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by dmitriysamoilov on 03.01.18.
 */

public class WatchingService extends Service {
    private static final String LOG_TAG = "WatchingService";

    private Handler mHandler = new Handler();
    private ActivityManager mActivityManager;
    private String text = null;
    private Timer timer;
    private BaseDataMaster dataMaster;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            String currentApp = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                        time - 1000 * 1000, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeUsed(),
                                usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(
                                mySortedMap.lastKey()).getPackageName();
                    }
                }
            } else {
                ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
                currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();

            }

            if (!currentApp.equals(text)) {
                text = currentApp;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new ToastShower().showToast(WatchingService.this, text);
                        if (dataMaster == null) {
                            dataMaster = BaseDataMaster.getDataMaster(WatchingService.this);
                        }
                        String sDate = String.valueOf(System.currentTimeMillis());
                        PackageManager packageManager= getApplicationContext().getPackageManager();
                        String appName = "";
                        try {
                           appName = (String) packageManager
                                    .getApplicationLabel(packageManager.getApplicationInfo(text, PackageManager.GET_META_DATA));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (appName.isEmpty()){
                            appName = text;
                        }
                        dataMaster.insertEvent(appName,sDate);
                    }
                });

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler = null;
        timer.cancel();
        Log.d(LOG_TAG, "onDestroy");

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "onTaskRemoved");
        Log.e("FLAGX : ", ServiceInfo.FLAG_STOP_WITH_TASK + "");
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 500,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

    }

}
