package com.appsx.childrensactivitycontrol.fragment;


import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.appsx.childrensactivitycontrol.app_name_util.ToastShower;
import com.appsx.childrensactivitycontrol.app_name_util.WatchingService;
import com.appsx.childrensactivitycontrol.database.SPHelper;
import com.willme.topactivity.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StatisticFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Intent serviceIntent;
    private Context context;
    private Switch startLissening;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);

        initialize(view);
        serviceIntent = new Intent(context, WatchingService.class);

        return view;
    }

    private void initialize(View view) {
        context = view.getContext();
        startLissening = (Switch) view.findViewById(R.id.sf_start_chac_switch);
        startLissening.setChecked(SPHelper.isServiceRunning(context));
        startLissening.setOnCheckedChangeListener(this);

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isAccessGranted()){
            if (compoundButton == startLissening) {
                if (!isChecked) {
                    context.stopService(serviceIntent);
                    SPHelper.setIsServiceRunning(context,false);
                    new ToastShower().showToast(context, "STOP");
                } else {
                    SPHelper.setIsServiceRunning(context,true);
                    context.startService(serviceIntent);
                }
            }
        }else {
            compoundButton.setChecked(false);
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



}
