package com.example.smartnotifyer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.ui.apps.AppsFragment;
import com.example.smartnotifyer.ui.limits.LimitFragment;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver{
    private AlarmHelper alarmHelper;
    public static int count = 0;
    public static List<App> selectedApps = new ArrayList<>();

    public long usageLimit;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        usageLimit = sharedPreferences.getLong("usageLimit", 0);

        if (selectedApps.size() != 0){
            Log.i("ALMALM", "Size: --> " + selectedApps.size());
            for (int i = 0; i < selectedApps.size(); i++) {
                Log.i("ALMALM", selectedApps.get(i).toString());
            }
        } else {
            Log.i("ALMALM", "Its emptyy");
        }

        Log.i("LIMITTAA", String.valueOf(usageLimit));

        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(context.getApplicationContext()); count++;

        if (count == 100){
            alarmHelper.stopAlarm();
        }
    }
}
