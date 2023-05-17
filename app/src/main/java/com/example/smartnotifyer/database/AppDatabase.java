package com.example.smartnotifyer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Stat.class, App.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StatDao statDao();
    public abstract AppDao appDao();
}