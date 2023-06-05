package com.project.digitime.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.project.digitime.database.App;
import com.project.digitime.database.Stat;
import com.project.digitime.ui.stats.UsageConverter;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver{
    private AlarmHelper alarmHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(context.getApplicationContext());
    }
}
