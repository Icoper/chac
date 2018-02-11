package com.appsx.childrensactivitycontrol.util;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsx.childrensactivitycontrol.R;
import com.appsx.childrensactivitycontrol.model.AppInfoListModel;

import java.util.ArrayList;

public class AlertDialogShower {
    private final String LOG_TAG = "AlertDialogShower";
    private Context context;

    public AlertDialogShower(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void showAlertDialog(AppInfoListModel appInfoListModel, FragmentManager fragmentManager) {

        View view = LayoutInflater.from(context).inflate(R.layout.alert_app_workperiod, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.alert_app_info_list_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        AppInfoArrayAdapter arrayAdapter = new AppInfoArrayAdapter(appInfoListModel.getAppInfos());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(arrayAdapter);

        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(view.getContext(), R.style.MyAlertDialogTheme));
        builder.setView(view);
        builder.setCancelable(false)
                .setNegativeButton(view.getContext().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.setTitle(appInfoListModel.getAppPackage());
        alertDialog.show();


    }

    /**
     * This is a modified adapter for displaying and processing the list of events in listview
     */
    public class AppInfoArrayAdapter extends RecyclerView.Adapter<AppInfoArrayAdapter.AppViewHolder> {
        ArrayList<AppInfoListModel.AppInfo> appInfoList;

        public AppInfoArrayAdapter(ArrayList<AppInfoListModel.AppInfo> appList) {
            this.appInfoList = appList;
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_info_list_item, viewGroup, false);
            AppViewHolder appViewHolder = new AppViewHolder(v);
            return appViewHolder;
        }

        @Override
        public void onBindViewHolder(AppViewHolder appViewHolder, int i) {
            appViewHolder.periodDay.setText(appInfoList.get(i).getDate());
            String periods = "";
            for (String s : appInfoList.get(i).getRunningEvents()) {
                periods += s + "\n";
            }
            appViewHolder.timeLaunch.setText(periods);
        }


        @Override
        public int getItemCount() {
            return appInfoList.size();
        }

        public class AppViewHolder extends RecyclerView.ViewHolder {
            TextView periodDay;
            TextView timeLaunch;


            AppViewHolder(View v) {
                super(v);
                periodDay = (TextView) v.findViewById(R.id.ai_date_tv);
                timeLaunch = (TextView) v.findViewById(R.id.ai_list_date_tv);

            }
        }
    }
}
