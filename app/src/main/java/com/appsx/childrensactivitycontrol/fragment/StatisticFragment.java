package com.appsx.childrensactivitycontrol.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ComponentName;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appsx.childrensactivitycontrol.R;
import com.appsx.childrensactivitycontrol.database.BaseDataMaster;
import com.appsx.childrensactivitycontrol.database.SPHelper;
import com.appsx.childrensactivitycontrol.model.AppInfoListModel;
import com.appsx.childrensactivitycontrol.model.AppListModel;
import com.appsx.childrensactivitycontrol.util.AlertDialogShower;
import com.appsx.childrensactivitycontrol.util.AppListHandler;
import com.appsx.childrensactivitycontrol.util.GlobalNames;
import com.appsx.childrensactivitycontrol.util.app_name.ToastShower;
import com.appsx.childrensactivitycontrol.util.app_name.WatchingService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StatisticFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static String[] timePeriodStore;

    private Intent serviceIntent;
    private Context context;
    private View fragmentView;
    private ArrayList<AppListModel> appList;
    private ArrayList<AppListModel> periodAppList;
    private ArrayList<AppListModel> dbAppList;

    // View
    private LinearLayoutManager layoutManager;
    private Switch startListening;
    private RecyclerView listRecyclerView;

    private LinearLayout linearLayoutPeriod;
    private TextView periodStatisticTv;
    private static TextView timePeriodFromTv;
    private static TextView timePeriodToTv;

    private LinearLayout linearLayoutPeriodFrom;
    private LinearLayout linearLayoutPeriodTo;
    private Button updateTimeFilterBtn;

    private ProgressBar loadAppsProgress;
    private ImageView moreDetTimePerIv;
    // use dataKey = "start" or dataKey = "end"
    private static String dataKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.statistic_fragment, container, false);
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

        linearLayoutPeriodFrom = (LinearLayout) getView().findViewById(R.id.ad_time_p_from);
        linearLayoutPeriodTo = (LinearLayout) getView().findViewById(R.id.ad_time_p_to);
        linearLayoutPeriodFrom.setOnClickListener(this);
        linearLayoutPeriodTo.setOnClickListener(this);
        timePeriodFromTv = (TextView) getView().findViewById(R.id.ad_time_p_from_tv);
        timePeriodToTv = (TextView) getView().findViewById(R.id.ad_time_p_to_tv);
        updateTimeFilterBtn = (Button) getView().findViewById(R.id.ad_time_update_btn);
        updateTimeFilterBtn.setOnClickListener(this);

        periodStatisticTv = (TextView) getView().findViewById(R.id.sf_time_period_text);
        setupPeriodTextView("", "");

        linearLayoutPeriod = (LinearLayout) getView().findViewById(R.id.sf_time_period);

        loadAppsProgress = (ProgressBar) getView().findViewById(R.id.sf_loadapss_progress);
        LoadAppsToListView longTask = new LoadAppsToListView();
        longTask.execute();
    }

    @SuppressLint("SetTextI18n")
    private void setupPeriodTextView(String start, String end) {
        // We set the first and last day of the previous month in textView
        if (start.isEmpty() || end.isEmpty()) {
            periodStatisticTv.setText(getString(R.string.time_period_all));
        } else {
            periodStatisticTv.setText(getString(R.string.time_period_start) + start +
                    getString(R.string.time_period_end) + end);
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        String manufacturer = "xiaomi";

        if (Build.VERSION.SDK_INT > 19) {
            if (!isAccessGranted()) {
                showMessageAlert(GlobalNames.ALERT_PERMISSION_HISTORY);
                compoundButton.setChecked(false);
            } else if (!SPHelper.isAutoStartPermGranted(context) && manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                compoundButton.setChecked(false);
                showMessageAlert(GlobalNames.ALERT_PERMISSION_AUTOSTART);
            } else {
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
            }
        } else {
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
        }

    }

    private void showMessageAlert(String mode) {

        View view = LayoutInflater.from(fragmentView.getContext()).inflate(R.layout.alert_permission_asker, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(fragmentView.getContext(), R.style.MyAlertDialogTheme));
        builder.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.alert_perm_message_tv);

        if (mode.equals(GlobalNames.ALERT_PERMISSION_AUTOSTART)) {

            textView.setText(fragmentView.getContext().getString(R.string.alert_perm_autostart_miui));
            builder.setCancelable(false)
                    .setPositiveButton(fragmentView.getContext().getString(R.string.confirm_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SPHelper.setisAutoStartPermGranted(context, true);
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton(fragmentView.getContext().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SPHelper.setisAutoStartPermGranted(fragmentView.getContext(), false);
                            dialog.cancel();
                        }
                    });


        } else if (mode.equals(GlobalNames.ALERT_PERMISSION_HISTORY)) {
            textView.setText(context.getString(R.string.alert_perm_history));
            builder.setCancelable(false)
                    .setPositiveButton(context.getString(R.string.confirm_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton(context.getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            case R.id.ad_time_p_from:
                openCalendar(id);
                break;
            case R.id.ad_time_p_to:
                openCalendar(id);
                break;
            case R.id.ad_time_update_btn:
                updateTimeFilter();
                break;
        }
    }

    private void updateTimeFilter() {
        try {
            if (!timePeriodStore[GlobalNames.START_PERIOD_ID].isEmpty() &&
                    !timePeriodStore[GlobalNames.END_PERIOD_ID].isEmpty()) {
                String startData = timePeriodStore[GlobalNames.START_PERIOD_ID];
                String endData = timePeriodStore[GlobalNames.END_PERIOD_ID];

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy");
                long filterDateStart = 0;
                long filterDataEnd = 0;
                try {
                    Date start = dateFormat.parse(startData);
                    filterDateStart = start.getTime();
                    Date end = dateFormat.parse(endData);
                    filterDataEnd = end.getTime();
                    if (filterDateStart > filterDataEnd) {
                        Toast.makeText(context, context.getString(R.string.time_period_wrong), Toast.LENGTH_SHORT).show();
                    } else {
                        periodStatisticTv.setText(getString(R.string.time_period_custom));
                        loadAppsProgress.setVisibility(View.VISIBLE);
                        LoadAppsToListView longTask = new LoadAppsToListView();
                        longTask.execute();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                periodStatisticTv.setText(getString(R.string.time_period_custom));
                loadAppsProgress.setVisibility(View.VISIBLE);
                LoadAppsToListView longTask = new LoadAppsToListView();
                longTask.execute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.time_period_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void openCalendar(int view_id) {
        // Проверяем userDataMap на null.
        if (timePeriodStore == null) {
            timePeriodStore = new String[2];
        }

        DialogFragment dialogFragment = new StatisticFragment.DatePicker();
        if (view_id == R.id.ad_time_p_from) {
            dataKey = "start";
        } else if (view_id == R.id.ad_time_p_to) {
            dataKey = "end";
        }
        dialogFragment.show(getFragmentManager(), "dataPicker");
    }

    private void showTimePeriodSelector() {
        ViewGroup.LayoutParams layoutParams = linearLayoutPeriod.getLayoutParams();
//        if (layoutParams.height != WindowManager.LayoutParams.WRAP_CONTENT) {
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        } else layoutParams.height = 85;
//
//        linearLayoutPeriod.setLayoutParams(layoutParams);

    }

    class LoadAppsToListView extends AsyncTask<Void, Void, ArrayList<AppListModel>> {

        @Override
        protected ArrayList<AppListModel> doInBackground(Void... noargs) {
            BaseDataMaster dataMaster = BaseDataMaster.getDataMaster(context);
            dbAppList = dataMaster.getEvents();

            return reformatAppList(dbAppList);
        }

        private ArrayList<AppListModel> reformatAppList(ArrayList<AppListModel> appListTemp) {
            AppListHandler appListHandler = new AppListHandler(GlobalNames.MODE_ALL_TIME, null, context);

            try {
                if (timePeriodStore.length != 0) {
                    appListHandler = new AppListHandler("", timePeriodStore, context);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            ArrayList<AppListModel> finalList = appListHandler.reformatList(appListTemp);
            for (AppListModel model : finalList) {
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
                    long time = Long.parseLong(model.getTime());

                    long seconds = (int) (time / 1000) % 60;
                    long min = (int) (time / (1000 * 60)) % 60;
                    long hours = (int) (time / (1000 * 60 * 60)) % 24;
                    model.setFormatedTime(
                            hours + " " + getString(R.string.hours_text) + " " +
                                    min + " " + getString(R.string.minutes_text) + " " +
                                    seconds + " " + getString(R.string.seconds_text));
                } else {
                    model.setFormatedTime("?");
                }

            }
            return finalList;
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
     * This class is used to show the user a DataPicker dialog
     * It implements the logic of storing a start and end date in HashMap
     */
    @SuppressLint("ValidFragment")
    public static class DatePicker extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        // The key is responsible for what date we are reading, start data or end data;
        // key can have 2 values: start or end;
        private String key = null;

        /**
         * Show User DataPiker dialog
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // determine the current date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Dialog picker = new DatePickerDialog(getActivity(), this,
                    year, month, day);
            return picker;
        }

        /**
         * The method stores the data received by the dataPicker from the user
         * *
         * * @param datePicker - calendar widget
         * * @param year is the year chosen by the user
         * * @param month - the month chosen by the user
         * * @param day - day selected by the user
         */
        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int year,
                              int month, int day) {
            // Declare the EditText variable, which we will later assign a link to
            // startData || endDataTv (EditText)
            // This is necessary in order to specify the selected date in the correct EditText
            TextView varEditText = timePeriodFromTv;
            String date = day + " / " + ++month + " / " + year;
            String formDate = "";
            key = dataKey;
            // Depending on the key, we assign varEditText the link we need
            if (key != null) {
                if (key.equals("start")) {
                    varEditText = timePeriodFromTv;
                    // Save the data in the periodStore
                    timePeriodStore[GlobalNames.START_PERIOD_ID] = date;
                    formDate = getString(R.string.time_period_start) + " 00:00" + getString(R.string.hours_text) +
                            " " + date;
                } else {
                    varEditText = timePeriodToTv;
                    // Save the data in the periodStore
                    timePeriodStore[GlobalNames.END_PERIOD_ID] = date;
                    formDate = getString(R.string.time_period_end) + " 24:00" + getString(R.string.hours_text) +
                            " " + date;
                }
            }
            // set new var in TextView
            varEditText.setText(formDate);
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
            appViewHolder.time.setText(appList.get(position).getFormatedTime());
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
                    // TODO Иконки приложения доступны только на устройстве с которого собирается статистика
                    Toast.makeText(getActivity().getBaseContext(), "In develop\n" + appList.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            appViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppListHandler appListHandler = new AppListHandler(GlobalNames.MODE_ALL_TIME, null, context);

                    try {
                        if (timePeriodStore.length != 0) {
                            appListHandler = new AppListHandler("", timePeriodStore, context);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    AppInfoListModel appInfo = appListHandler.getAppInfoByPackage(dbAppList, appList.get(position).getName());
                    new AlertDialogShower(fragmentView.getContext()).showAlertDialog(appInfo, getFragmentManager());

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
