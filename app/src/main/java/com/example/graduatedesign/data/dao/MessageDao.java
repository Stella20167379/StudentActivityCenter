package com.example.graduatedesign.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.graduatedesign.data.model.College;
import com.example.graduatedesign.data.model.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Message> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOne(Message message);

    @Query("SELECT * FROM message WHERE receiverId=:id")
    LiveData<List<Message>> loadAll(Integer id);

    @Delete
    void deleteOne(Message message);

    @Query("DELETE FROM message WHERE id IN (:ids)")
    void deleteListByIds(List<Integer> ids);

    @Query("UPDATE message SET sender=:newNickname WHERE senderId=:senderId")
    void updateSenderNickname(String newNickname,Integer senderId);
}
