package com.example.smartnotifyer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.ui.apps.AppsFragment;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver{
    private AlarmHelper alarmHelper;
    public static int count = 0;
    public static List<App> selectedApps = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALMALM", "Size: --> " + selectedApps.size());

        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(context.getApplicationContext()); count++;

        if (count == 100){
            alarmHelper.stopAlarm();
        }
    }
}
