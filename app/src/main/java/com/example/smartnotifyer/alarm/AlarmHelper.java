package com.example.smartnotifyer.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmHelper {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public void setAlarmInNextMinute(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(alarmIntent);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            //Setting alarm in 5 seconds
            long msToOff = System.currentTimeMillis() + 1000 * 5;
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(msToOff, null), alarmIntent);;
        }
    }

    public void stopAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }
}







