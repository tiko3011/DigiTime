package com.example.smartnotifyer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {
    @Query("SELECT * FROM Apps ORDER BY app_time DESC")
    List<App> getAllApps();

    @Insert
    void insertApp(App app);

    @Update
    void updateApp(App app);

    @Delete
    void delete(App app);

    @Query("DELETE FROM apps")
    void deleteAll();
}