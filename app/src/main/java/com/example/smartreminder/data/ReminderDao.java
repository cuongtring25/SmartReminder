package com.example.smartreminder.data;

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
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("DELETE FROM reminders")
    void deleteAllReminders();

    @Query("SELECT * FROM reminders ORDER BY dateTime ASC")
    LiveData<List<Reminder>> getAllReminders();

    @Query("SELECT * FROM reminders WHERE category = :category ORDER BY dateTime ASC")
    LiveData<List<Reminder>> getRemindersByCategory(String category);
}
