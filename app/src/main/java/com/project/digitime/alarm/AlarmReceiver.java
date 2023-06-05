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
    public static int count = 0;
    public static List<App> selectedApps = new ArrayList<>();
    public static List<Stat> stats = new ArrayList<>();
    public static List<Stat> selectedStats = new ArrayList<>();

    public static long usageLimit = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        usageLimit = sharedPreferences.getLong("usageLimit", 0);
        UsageConverter.deleteDuplicates(stats);
        getSelectedStats();
        log();


        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(context.getApplicationContext()); count++;

//        if (count == 20){
//            alarmHelper.stopAlarm();
//        }
    }

    public void getSelectedStats(){
        selectedStats.clear();
        for (int i = 0; i < selectedApps.size(); i++) {
            for (int j = 0; j < stats.size(); j++) {
                if (selectedApps.get(i).appName.equals(stats.get(j).statName)){
                    selectedStats.add(stats.get(j));
                }
            }
        }
    }

    public void log(){
        if (selectedApps.size() != 0){
            Log.i("ALMALM", "Size: --> " + selectedApps.size());
            for (int i = 0; i < selectedApps.size(); i++) {
                Log.i("ALMALM", selectedApps.get(i).toString());
            }
        } else {
            Log.i("ALMALM", "Apps is empty");
        }

        if (selectedStats.size() != 0){
            Log.i("ALMALM", "Size: --> " + selectedStats.size());
            for (int i = 0; i < selectedStats.size(); i++) {
                Log.i("ALMALM", selectedStats.get(i).toString());
            }
        } else {
            Log.i("ALMALM", "Stats is empty");
        }

        Log.i("ALMALM", "");
    }
}
