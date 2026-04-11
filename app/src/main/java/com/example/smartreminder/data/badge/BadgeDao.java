package com.example.smartreminder.data.badge;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Badge badge);

    @Update
    void update(Badge badge);

    @Delete
    void delete(Badge badge);

    @Query("SELECT * FROM badges")
    List<Badge> getAllBadges();

    @Query("SELECT * FROM badges WHERE id = :id")
    Badge getById(int id);

    @Query("UPDATE badges SET icon_url = :icon_url, xp_reward = :xp_reward WHERE id = :id")
    void updateBadge(int id, String icon_url, int xp_reward);

}
