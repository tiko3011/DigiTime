package com.example.smartnotifyer.mvvm;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartnotifyer.database.AppDatabase;
import com.example.smartnotifyer.database.DatabaseClient;
import com.example.smartnotifyer.database.Stat;

import java.util.List;

import javax.xml.namespace.QName;

public class StatsViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;
    private MutableLiveData<List<Stat>> stats;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        stats = new MutableLiveData<>();
        appDatabase = DatabaseClient.getInstance(getApplication()).getAppDatabase();
        AsyncTask.execute(this::refreshStatList);
    }

    public LiveData<List<Stat>> getStats() {
        return stats;
    }

    public List<Stat> getAllStats(){
        refreshStatList();
        return appDatabase.statDao().getAllStats();
    }

    public void deleteDuplicates(){
        AsyncTask.execute(() -> {
            List<Stat> statList = appDatabase.statDao().getAllStats();

            for (int i = 0; i < statList.size(); i++) {
                String nameI = statList.get(i).getStatName();
                long timeI = statList.get(i).getStatTime();

                for (int j = 0; j < i; j++) {
                    String nameJ = statList.get(j).getStatName();
                    long timeJ = statList.get(j).getStatTime();

                    if (nameI.equals(nameJ)){
                        appDatabase.statDao().getItemByName(nameJ).setStatTime(timeI + timeJ);
                        appDatabase.statDao().delete(statList.get(i));
                        break;
                    }
                }
            }

            refreshStatList();
        });
    }

    public void addStat(@NonNull String statName, long statTime) {
        AsyncTask.execute(() -> {
            appDatabase.statDao().insertStat(new Stat(statName, statTime));
            refreshStatList();
        });
    }

    public void addStatsFromSystemDaily(long startTime, long endTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        for (int i = 0; i < usageStatsList.size(); i++) {
            UsageStats usageStats = usageStatsList.get(i);

            if (usageStats.getTotalTimeInForeground() / 60000 > 0 && usageStats.getLastTimeUsed() >= startTime) {
                AsyncTask.execute(() -> {
                        appDatabase.statDao().insertStat(new Stat(usageStats.getPackageName(), usageStats.getTotalTimeInForeground()));
                        refreshStatList();
                });
            }
        }
    }

    public void addStatsFromSystemWeekly(long startTime, long endTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime);

        for (int i = 0; i < usageStatsList.size(); i++) {
            UsageStats usageStats = usageStatsList.get(i);

            if (usageStats.getTotalTimeInForeground() / 60000 > 0) {
                AsyncTask.execute(() -> {
                    appDatabase.statDao().insertStat(new Stat(usageStats.getPackageName(), usageStats.getTotalTimeInForeground()));
                    refreshStatList();
                });
            }
        }
    }

    public void deleteStat(Stat stat) {
        AsyncTask.execute(() -> {
            appDatabase.statDao().delete(stat);
            refreshStatList();
        });
    }

    public void deleteAllStats() {
        AsyncTask.execute(() -> {
            appDatabase.statDao().deleteAll();
            refreshStatList();
        });
    }

    public void refreshStatList() {
        List<Stat> updateList = appDatabase.statDao().getAllStats();
        stats.postValue(updateList);
    }

    public void readRequest() {
        AsyncTask.execute(this::refreshStatList);
    }

    public void updateStat(Stat stat) {
        AsyncTask.execute(() -> {
            appDatabase.statDao().updateStat(stat);
            refreshStatList();
        });
    }
}