package com.project.digitime.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Stat.class, App.class}, version = 9)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StatDao statDao();
    public abstract AppDao appDao();
}