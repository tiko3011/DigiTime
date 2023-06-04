package com.example.smartnotifyer.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.smartnotifyer.MainActivity;
import com.example.smartnotifyer.R;
import com.example.smartnotifyer.database.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmHelper {
    private long usagelimit;
    private List<Stat> stats = new ArrayList<>();
    public static long usage;
    boolean isLimitReached = false;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public static final String CHANNEL_ID = "DIGITIME_ID";
    private int alarmCount = 0;

    public void setAlarmInNextMinute(Context context) { alarmCount++;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(alarmIntent);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            //Setting alarm in 5 seconds
            long msToOff = System.currentTimeMillis() + 1000 * 5;
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(msToOff, null), alarmIntent);;
        }

        if (alarmCount > 0){
            usagelimit = AlarmReceiver.usageLimit;
            stats.clear();
            stats.addAll(AlarmReceiver.selectedStats);
        }

        if (stats.size() != 0){
            usage = 0;
            for (int i = 0; i < stats.size(); i++) {
                usage += stats.get(i).statTime / 60000;
            }

            Log.i("Limits", "UsageLimit: --> " + usagelimit);
            Log.i("Limits", "Usage: --> " + usage);
            isLimitReached = usage >= usagelimit;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isLimitReached) {
            sentNotification("Limit", "You reached usage limit", context);

            if(alarmManager.canScheduleExactAlarms()) {
                //Setting alarm in 1 minute
                long msToOff = System.currentTimeMillis() + 1000 * 60;
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(msToOff, null), alarmIntent);;
            }
        }


    }

    public void stopAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void sentNotification(String title, String text, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("data", "Some data");

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel =
                notificationManager.getNotificationChannel(CHANNEL_ID);

        if (notificationChannel == null){
            int importance = NotificationManager.IMPORTANCE_HIGH;

            notificationChannel = new NotificationChannel(CHANNEL_ID, "Description", importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Random random = new Random();
        int notificationId = random.nextInt(1000);
        notificationManager.notify(notificationId, builder.build());
    }
}







