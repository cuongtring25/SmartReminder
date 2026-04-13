package com.example.smartreminder.data.userbadge;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserBadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserBadge userBadge);

    @Update
    void update(UserBadge userBadge);

    @Delete
    void delete(UserBadge userBadge);

    @Query("SELECT * FROM user_badges WHERE user_id = :userId")
    List<UserBadge> getBadgesForUser(int userId);

    @Query("SELECT COUNT(*) FROM user_badges WHERE user_id = :userId AND badge_id = :badgeId")
    int countUserBadge(int userId, int badgeId);
}
