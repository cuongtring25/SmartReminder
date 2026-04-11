package com.example.smartreminder.data.reminderlog;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReminderLogDao {
    @Insert
    void insert(ReminderLog log);

    @Query("SELECT * FROM reminder_logs WHERE reminder_id = :reminderId ORDER BY actioned_at DESC")
    LiveData<List<ReminderLog>> getLogsByReminderId(String reminderId);

    @Query("SELECT * FROM reminder_logs WHERE user_id = :userId ORDER BY actioned_at DESC")
    LiveData<List<ReminderLog>> getLogsByUser(String userId);

    @Query("SELECT COUNT(*) FROM reminder_logs WHERE action = 'completed' AND user_id = :userId")
    LiveData<Integer> getCompletedCount(String userId);
}
