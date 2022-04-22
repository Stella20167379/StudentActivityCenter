package com.example.graduatedesign.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.graduatedesign.data.model.College;

import java.util.List;

@Dao
public interface CollegeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<College> colleges);

    @Query("SELECT * FROM college")
    List<College> getAllCollege();

    @Query("DELETE FROM college")
    int clearAll();
}
