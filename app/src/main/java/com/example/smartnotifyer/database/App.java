package com.example.smartnotifyer.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Apps")
public class App implements Comparable<App>{

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "app_name")
    public String appName;

    @ColumnInfo(name = "app_time")
    public long appUsageWeekly;

    @ColumnInfo(name = "app_checked")
    public boolean isChecked;

    public App(@NonNull String appName, long appUsageWeekly) {
        this.appName = appName;
        this.appUsageWeekly = appUsageWeekly;
    }

    @NonNull
    public String getAppName() {
        return appName;
    }

    public void setAppName(@NonNull String appName) {
        this.appName = appName;
    }

    public long getAppUsageWeekly() {
        return appUsageWeekly;
    }

    public void setAppUsageWeekly(long appUsageWeekly) {
        this.appUsageWeekly = appUsageWeekly;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "App Name: --> " + appName;
    }

    @Override
    public int compareTo(App o) {
        return Long.compare(o.appUsageWeekly, this.appUsageWeekly);
    }
}
