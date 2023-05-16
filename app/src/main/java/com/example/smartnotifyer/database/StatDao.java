package com.example.smartnotifyer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StatDao {
    @Query("SELECT * FROM Stats")
    List<Stat> getAllStats();

    @Insert
    void insertStat(Stat stat);

    @Update
    void updateStat(Stat stat);

    @Delete
    void delete(Stat stat);

    @Query("DELETE FROM stats")
    void deleteAll();

    @Query("SELECT * FROM Stats WHERE stat_id = :statId")
    Stat getStatById(int statId);
}