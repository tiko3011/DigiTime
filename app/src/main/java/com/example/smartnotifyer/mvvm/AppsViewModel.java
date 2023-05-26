package com.example.smartnotifyer.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.database.AppDatabase;
import com.example.smartnotifyer.database.DatabaseClient;
import com.example.smartnotifyer.database.Stat;

import java.util.List;

public class AppsViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;
    private MutableLiveData<List<App>> apps;

    public AppsViewModel(@NonNull Application application) {
        super(application);
        apps = new MutableLiveData<>();
        appDatabase = DatabaseClient.getInstance(getApplication()).getAppDatabase();
        AsyncTask.execute(this::refreshAppList);
    }

    public LiveData<List<App>> getApps() {
        return apps;
    }

    public List<App> getAllApps(){
        return appDatabase.appDao().getAllApps();
    }

    public void addApp(@NonNull String appName) {
        AsyncTask.execute(() -> {
            appDatabase.appDao().insertApp(new App(appName));
            refreshAppList();
        });
    }

    public void addInstalledApps() {
        PackageManager packageManager = getApplication().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);


        @SuppressLint("QueryPermissionsNeeded")
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            String appName = resolveInfo.loadLabel(packageManager).toString();
            String packageName = resolveInfo.activityInfo.packageName;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.appDao().insertApp(new App(appName));
                    refreshAppList();
                }
            });
        }

    }

    public void deleteApp(App app) {
        AsyncTask.execute(() -> {
            appDatabase.appDao().delete(app);
            refreshAppList();
        });
    }

    public void deleteAllApps() {
        AsyncTask.execute(() -> {
            appDatabase.appDao().deleteAll();
            refreshAppList();
        });
    }

    public void refreshAppList() {
        List<App> updateList = appDatabase.appDao().getAllApps();
        apps.postValue(updateList);
    }

    public void readRequest() {
        AsyncTask.execute(this::refreshAppList);
    }

    public void updateApp(App app) {
        AsyncTask.execute(() -> {
            appDatabase.appDao().updateApp(app);
            refreshAppList();
        });
    }
}