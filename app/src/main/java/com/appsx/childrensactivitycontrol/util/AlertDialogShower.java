package com.appsx.childrensactivitycontrol.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.appsx.childrensactivitycontrol.R;

/**
 * Created by hp on 27.01.2018.
 */

public class AlertDialogShower {
    private final String LOG_TAG = "AlertDialogShower";
    private Context context;

    public AlertDialogShower(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String[] period, String appPackage) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.alert_app_workperiod, null);
        TextView textView = (TextView)view.findViewById(R.id.text_alert);
        textView.setText(appPackage);

        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog));
        builder.setView(view);
        builder.setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(appPackage);
        alertDialog.show();
    }

    private void buildGraph(String[] period, String appPackage, View view) {
//        GraphView graphView = (GraphView) view.findViewById(R.id.alert_graph);
//
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.alert_layout_graph);
//        layout.addView(graphView);
    }

}
