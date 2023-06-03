package com.example.smartnotifyer.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Stats")
public class Stat implements Comparable<Stat>{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stat_id")
    public int statId;

    @NonNull
    @ColumnInfo(name = "stat_name")
    public String statName;

    @ColumnInfo(name = "stat_time")
    public long statTime;

    public Stat(@NonNull String statName, long statTime) {
        this.statName = statName;
        this.statTime = statTime;
    }

    @NonNull
    public String getStatName() {
        return statName;
    }

    public void setStatName(@NonNull String statName) {
        this.statName = statName;
    }

    public long getStatTime() {
        return statTime;
    }

    public void setStatTime(long statTime) {
        this.statTime = statTime;
    }

    @Override
    public String toString() {
        return "Stat name: --> " + statName;
    }

    @Override
    public int compareTo(Stat o) {
        return Long.compare(o.statTime, this.statTime);
    }
}
