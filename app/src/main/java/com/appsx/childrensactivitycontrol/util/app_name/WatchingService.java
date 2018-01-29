package com.appsx.childrensactivitycontrol.util.app_name;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.appsx.childrensactivitycontrol.database.BaseDataMaster;
import com.appsx.childrensactivitycontrol.database.SPHelper;
import com.appsx.childrensactivitycontrol.util.screen.ScreenBroadCastReceiver;
import com.appsx.childrensactivitycontrol.util.screen.ScreenStateHelper;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class WatchingService extends Service {
    private static final String LOG_TAG = "WatchingService";

    private static boolean screenOn = false;
    private ScreenStateHelper screenStateHelper;
    private Handler mHandler = new Handler();
    private ActivityManager mActivityManager;
    private String text = null;
    private Timer timer;
    private BaseDataMaster dataMaster;
    private BroadcastReceiver screenStateReceiver;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        screenStateReceiver = new ScreenBroadCastReceiver();

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, screenStateFilter);
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
        if (screenStateHelper == null) {
            screenStateHelper = new ScreenStateHelper(getApplicationContext());
        }
        if (dataMaster == null) {
            dataMaster = BaseDataMaster.getDataMaster(WatchingService.this);
        }
        screenOn = screenStateHelper.isScreenOn();
        SPHelper.setIsScreenOn(WatchingService.this, screenOn);
        dataMaster.insertScreenState(String.valueOf(System.currentTimeMillis()));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenStateHelper == null) {
            screenStateHelper = new ScreenStateHelper(getApplicationContext());
        }
        if (dataMaster == null) {
            dataMaster = BaseDataMaster.getDataMaster(WatchingService.this);
        }
        screenOn = screenStateHelper.isScreenOn();
        SPHelper.setIsScreenOn(WatchingService.this, screenOn);
        updateEventDB();
        dataMaster.insertScreenState(String.valueOf(System.currentTimeMillis()));
        unregisterReceiver(screenStateReceiver);
        mHandler = null;
        timer.cancel();
        Log.d(LOG_TAG, "onDestroy");

    }

    private void updateEventDB() {
        Log.d(LOG_TAG, "new Task");

        if (dataMaster == null) {
            dataMaster = BaseDataMaster.getDataMaster(WatchingService.this);
        }
        String sDate = String.valueOf(System.currentTimeMillis());
        dataMaster.insertEvent(text, sDate);

    }

    private String getTaskName() {
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

        return currentApp;
    }

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
        updateEventDB();
        super.onTaskRemoved(rootIntent);

    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            if (screenStateHelper == null) {
                screenStateHelper = new ScreenStateHelper(getApplicationContext());
            }
            if (dataMaster == null) {
                dataMaster = BaseDataMaster.getDataMaster(WatchingService.this);
            }
            try {
                screenOn = SPHelper.isIsScreenOn(WatchingService.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
//          e  mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    updateEventDB();
//                }
//            });

//            if (screenOn != screenStateHelper.isScreenOn()) {
//                screenOn = screenStateHelper.isScreenOn();
//                SPHelper.setIsScreenOn(WatchingService.this, screenOn);
//                dataMaster.insertScreenState(String.valueOf(System.currentTimeMillis()));
//
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateEventDB();
//                    }
//                });
//            }


            if (!getTaskName().equals(text)) {
                text = getTaskName();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateEventDB();
                    }
                });

            }

        }
    }

}
