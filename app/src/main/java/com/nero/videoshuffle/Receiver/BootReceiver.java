package com.nero.videoshuffle.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Date;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "my boot", Toast.LENGTH_SHORT).show();
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {

            Notification notification = new Notification.Builder(context)
                    .setContentText("OS launched completed")
                    .setContentTitle("启动标题")
                    .setWhen(new Date().getTime())
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            Intent newIntent = new Intent(context, BroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_ONE_SHOT);

            notification.contentIntent = pendingIntent;
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0, notification);

        }
    }
}
