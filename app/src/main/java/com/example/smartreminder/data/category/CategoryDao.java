package com.example.smartreminder.data.category;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE is_system = 1 AND is_deleted = 0 ORDER BY display_order ASC")
    LiveData<List<Category>> getSystemCategories();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category getById(int id);
    @Query("SELECT * FROM categories WHERE is_deleted = 0")
    List<Category> getAllCategories();
}
