package com.appsx.childrensactivitycontrol.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.appsx.childrensactivitycontrol.activity.MainActivity;
import com.appsx.childrensactivitycontrol.R;
import com.appsx.childrensactivitycontrol.activity.PasswordActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by hp on 11.02.2018.
 */

public class NotificationWorker {
    private static final int NOTIFICATION_ID = 25;
    private NotificationCompat.Builder builder;
    private Context context;

    public NotificationWorker(Context context) {
        this.context = context;
    }

    public void showNotification() {
        Intent notificationIntent = new Intent(context, PasswordActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(contentIntent)
                .setContentText(context.getString(R.string.notify_service_work));
        Notification notification = builder.build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void changeNotificationMessage(boolean isWork) {
        if (builder == null) {
            showNotification();
        }
        if (isWork) {
            builder.setContentText(context.getString(R.string.notify_service_work));
        } else builder.setContentText(context.getString(R.string.notify_service_not_work));

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void stopShowNotification() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
