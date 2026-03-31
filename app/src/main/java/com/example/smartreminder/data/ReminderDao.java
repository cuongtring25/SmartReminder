package com.example.smartreminder.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("DELETE FROM reminders")
    void deleteAllReminders();

    @Query("SELECT * FROM reminders WHERE is_deleted = 0 ORDER BY due_date ASC")
    LiveData<List<Reminder>> getAllReminders();

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND is_deleted = 0 ORDER BY due_date ASC")
    LiveData<List<Reminder>> getRemindersByUser(String userId);

    @Query("SELECT * FROM reminders WHERE category_id = :categoryId AND is_deleted = 0")
    LiveData<List<Reminder>> getRemindersByCategory(String categoryId);

    @Query("SELECT * FROM reminders WHERE status = :status AND is_deleted = 0")
    LiveData<List<Reminder>> getRemindersByStatus(String status);

    // Stats for Dashboard
    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND DATE(due_date/1000, 'unixepoch') = DATE(:today/1000, 'unixepoch') AND is_deleted = 0")
    LiveData<Integer> getTodayTasksCount(String userId, long today);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'done' AND DATE(due_date/1000, 'unixepoch') = DATE(:today/1000, 'unixepoch') AND is_deleted = 0")
    LiveData<Integer> getTodayCompletedCount(String userId, long today);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'done' AND is_deleted = 0")
    LiveData<Integer> getTotalCompletedCount(String userId);
}
