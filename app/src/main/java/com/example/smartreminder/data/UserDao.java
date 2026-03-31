package com.example.smartreminder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    User getById(String userId);

    @Query("UPDATE users SET current_streak = 0 WHERE user_id = :userId")
    void resetStreak(String userId);

    @Query("UPDATE users SET current_streak = current_streak + 1, last_completion_date = :today, longest_streak = CASE WHEN current_streak + 1 > longest_streak THEN current_streak + 1 ELSE longest_streak END WHERE user_id = :userId")
    void incrementStreak(String userId, Date today);

    @Query("UPDATE users SET current_streak = 1, last_completion_date = :today WHERE user_id = :userId")
    void startNewStreak(String userId, Date today);
}
