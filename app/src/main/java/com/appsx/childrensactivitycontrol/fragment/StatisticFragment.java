package com.appsx.childrensactivitycontrol.fragment;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appsx.childrensactivitycontrol.database.BaseDataMaster;
import com.appsx.childrensactivitycontrol.model.AppListModel;
import com.appsx.childrensactivitycontrol.util.app_name.ToastShower;
import com.appsx.childrensactivitycontrol.util.app_name.WatchingService;
import com.appsx.childrensactivitycontrol.database.SPHelper;
import com.appsx.childrensactivitycontrol.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StatisticFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Intent serviceIntent;
    private Context context;
    private View fragmentView;
    private ArrayList<AppListModel> appList;

    // View
    private LinearLayoutManager layoutManager;
    private Switch startListening;
    private RecyclerView listRecyclerView;
    private TextView periodStatisticTv;
    private LinearLayout linearLayoutPeriod;
    private ProgressBar loadAppsProgress;
    private ImageView moreDetTimePerIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.statistic_fragment, container, false);
        fragmentView = v;
        context = getActivity().getApplicationContext();

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        serviceIntent = new Intent(context, WatchingService.class);

    }


    // init all view elements
    private void initialize() {
        startListening = (Switch) getView().findViewById(R.id.sf_start_chac_switch);
        startListening.setHighlightColor(getResources().getColor(R.color.colorBlue));
        startListening.setChecked(SPHelper.isServiceRunning(context));
        startListening.setOnCheckedChangeListener(this);

        layoutManager = new LinearLayoutManager(context);
        listRecyclerView = (RecyclerView) getView().findViewById(R.id.apps_list);
        listRecyclerView.setLayoutManager(layoutManager);
        listRecyclerView.setHasFixedSize(true);

        periodStatisticTv = (TextView) getView().findViewById(R.id.sf_time_period_text);
        setupPeriodTextView("", "");
        moreDetTimePerIv = (ImageView) getView().findViewById(R.id.sf_time_period_more);
        moreDetTimePerIv.setOnClickListener(this);
        linearLayoutPeriod = (LinearLayout) getView().findViewById(R.id.sf_time_period);

        loadAppsProgress = (ProgressBar) getView().findViewById(R.id.sf_loadapss_progress);
        LoadAppsToListView longTask = new LoadAppsToListView();
        longTask.execute();
    }

    @SuppressLint("SetTextI18n")
    private void setupPeriodTextView(String start, String end) {
        // We set the first and last day of the previous month in textView
        if (start.isEmpty() || end.isEmpty()) {
            periodStatisticTv.setText(getString(R.string.time_period_today));
        } else {
            periodStatisticTv.setText(getString(R.string.time_period_start) + start +
                    getString(R.string.time_period_end) + end);
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isAccessGranted()) {
            if (compoundButton == startListening) {
                if (!isChecked) {
                    context.stopService(serviceIntent);
                    SPHelper.setIsServiceRunning(context, false);
                    new ToastShower().showToast(context, "STOP");
                } else {
                    SPHelper.setIsServiceRunning(context, true);
                    context.startService(serviceIntent);
                }
            }
        } else {
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sf_time_period_more:
                showTimePeriodSelector();
                break;
        }
    }

    private void showTimePeriodSelector() {
        ViewGroup.LayoutParams layoutParams = linearLayoutPeriod.getLayoutParams();
        if (layoutParams.height != WindowManager.LayoutParams.WRAP_CONTENT) {
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else layoutParams.height = 85;

        linearLayoutPeriod.setLayoutParams(layoutParams);
    }

    class LoadAppsToListView extends AsyncTask<Void, Void, ArrayList<AppListModel>> {

        @Override
        protected ArrayList<AppListModel> doInBackground(Void... noargs) {
            BaseDataMaster dataMaster = BaseDataMaster.getDataMaster(context);
            ArrayList<AppListModel> appListTemp = dataMaster.getEvents();
            for (AppListModel model : appListTemp) {
                String checkedString = "";
                try {
                    checkedString = model.getDataEnd();
                    if (checkedString == null) {
                        checkedString = "";
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    checkedString = "";
                }
                if (!checkedString.isEmpty()) {
                    long timeEnd = Long.parseLong(model.getDataEnd());
                    long timeStart = Long.parseLong(model.getDataStart());
                    long time = timeEnd - timeStart;
                    long seconds = (int) (time / 1000) % 60;
                    long min = (int) (time / (1000 * 60)) % 60;
                    long hours = (int) (time / (1000 * 60 * 60)) % 24;
                    model.setTime(hours + ":" + min + ":" + seconds);
                }

            }
            return appListTemp;
        }

        @Override
        protected void onPostExecute(ArrayList<AppListModel> list) {
            appList = new ArrayList<>();
            appList = list;
            AppArrayAdapter appAdapter = new AppArrayAdapter(appList);
            listRecyclerView.setAdapter(appAdapter);
            loadAppsProgress.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * This is a modified adapter for displaying and processing the list of events in listview
     */
    public class AppArrayAdapter extends RecyclerView.Adapter<AppArrayAdapter.AppViewHolder> {
        ArrayList<AppListModel> appsList;

        public AppArrayAdapter(ArrayList<AppListModel> appsList) {
            this.appsList = appsList;
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_list_item, viewGroup, false);
            AppViewHolder appViewHolder = new AppViewHolder(v);
            return appViewHolder;
        }

        @Override
        public void onBindViewHolder(AppViewHolder appViewHolder, int i) {
            final int position = i;

            appViewHolder.appName.setText(appList.get(position).getName());
            appViewHolder.time.setText(appList.get(position).getTime());
            try {
                Drawable _icon = context.getPackageManager().getApplicationIcon(appList.get(position).getAppPackage());
                appViewHolder.icon.setImageDrawable(_icon);
            } catch (PackageManager.NameNotFoundException e) {
                appViewHolder.icon.setBackgroundResource(R.drawable.ic_info_black_48dp);
                e.printStackTrace();
            }
            appViewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity().getBaseContext(), "In develop\n" + appList.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            appViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity().getBaseContext(), "In develop\n" + appList.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return appsList.size();
        }

        public class AppViewHolder extends RecyclerView.ViewHolder {
            TextView appName;
            TextView time;
            ImageView icon;
            ImageView more;

            AppViewHolder(View v) {
                super(v);
                appName = (TextView) v.findViewById(R.id.app_name_list);
                time = (TextView) v.findViewById(R.id.app_time_list);
                icon = (ImageView) v.findViewById(R.id.app_icon_list);
                more = (ImageView) v.findViewById(R.id.app_more_list);
            }
        }
    }

}
