package com.example.smartreminder.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecurrenceRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecurrenceRule rule);

    @Update
    void update(RecurrenceRule rule);

    @Delete
    void delete(RecurrenceRule rule);

    @Query("SELECT * FROM recurrence_rules WHERE user_id = :userId AND is_active = 1")
    LiveData<List<RecurrenceRule>> getActiveRulesByUser(int userId);

    @Query("SELECT * FROM recurrence_rules WHERE id = :id LIMIT 1")
    RecurrenceRule getById(int id);
}
