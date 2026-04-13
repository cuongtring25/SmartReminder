package com.example.smartreminder.data.reminder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    Reminder getById(int id);

    @Query("SELECT * FROM reminders WHERE is_deleted = 0 AND alarm_enabled = 1 " +
            "AND status IN ('pending', 'snoozed') AND (remind_at > :nowMillis OR due_date > :nowMillis)")    List<Reminder> getFutureAlarmReminders(long nowMillis);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'completed' AND is_deleted = 0")
    int countCompletedReminders(int userId);

    @Query("SELECT DISTINCT strftime('%Y-%m-%d', due_date/1000, 'unixepoch', 'localtime') " +
            "FROM reminders WHERE user_id = :userId AND status = 'completed' AND is_deleted = 0 " +
            "ORDER BY 1 ASC")
    List<String> getCompletedLocalDayKeys(int userId);
    @Query("SELECT * FROM reminders WHERE is_deleted = 0 ORDER BY due_date ASC")
    LiveData<List<Reminder>> getAllReminders();

    // Stats for Dashboard
    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND DATE(due_date/1000, 'unixepoch', 'localtime') = DATE(:today/1000, 'unixepoch', 'localtime') AND is_deleted = 0")
    LiveData<Integer> getTodayTasksCount(int userId, long today);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'completed' AND DATE(due_date/1000, 'unixepoch', 'localtime') = DATE(:today/1000, 'unixepoch', 'localtime') AND is_deleted = 0")
    LiveData<Integer> getTodayCompletedCount(int userId, long today);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'completed' AND is_deleted = 0")
    LiveData<Integer> getTotalCompletedCount(int userId);

    // Weekly stats
    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId " +
            "AND due_date >= :startOfWeek AND due_date <= :endOfWeek AND is_deleted = 0")
    LiveData<Integer> getRangeTasksCount(int userId, long startOfWeek, long endOfWeek);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'completed' " +
            "AND due_date >= :startOfWeek AND due_date <= :endOfWeek AND is_deleted = 0")
    LiveData<Integer> getRangeCompletedCount(int userId, long startOfWeek, long endOfWeek);

    // Dữ liệu cho biểu đồ: Đếm số task hoàn thành theo từng ngày trong khoảng thời gian
    @Query("SELECT DATE(due_date/1000, 'unixepoch', 'localtime') as date, COUNT(*) as completedCount " +
            "FROM reminders WHERE user_id = :userId AND status = 'completed' " +
            "AND due_date >= :start AND due_date <= :end AND is_deleted = 0 " +
            "GROUP BY date ORDER BY date ASC")
    LiveData<List<DayStat>> getWeeklyCompletedStats(int userId, long start, long end);

    @Query("SELECT * FROM reminders WHERE user_id = :userId " +
            "AND DATE(due_date/1000, 'unixepoch', 'localtime') = DATE(:today/1000, 'unixepoch', 'localtime') " +
            "AND is_deleted = 0 ORDER BY due_date ASC")
    List<Reminder> getTodayReminders(int userId, long today);

    @Query("UPDATE reminders SET status = 'snoozed' " +
            "WHERE user_id = :userId " +
            "AND status = 'pending' " +
            "AND DATE(due_date/1000, 'unixepoch', 'localtime') < DATE(:today/1000, 'unixepoch', 'localtime') " +
            "AND is_deleted = 0")
    void snoozePastPendingReminders(int userId, long today);
}
