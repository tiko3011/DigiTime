package com.example.smartnotifyer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.smartnotifyer.ui.UsageConverter;

@Entity(tableName = "Apps")
public class App implements Comparable<App>{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "app_id")
    public int appId;

    @ColumnInfo(name = "app_name")
    public String appName;

    @ColumnInfo(name = "app_time")
    public long appUsageWeekly;

    @ColumnInfo(name = "app_checked")
    public boolean isChecked;

    public App(String appName, long appUsageWeekly) {
        this.appName = appName;
        this.appUsageWeekly = appUsageWeekly;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
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
        return "App{" +
                "appId=" + appId +
                ", appName='" + appName + '\'' +
                ", appUsageWeekly=" + appUsageWeekly +
                ", isChecked=" + isChecked +
                '}';
    }

    @Override
    public int compareTo(App o) {
        return Long.compare(o.appUsageWeekly, this.appUsageWeekly);
    }
}
