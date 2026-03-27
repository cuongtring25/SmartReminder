package com.example.smartreminder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String title;
    private String description;
    private long dateTime; // Timestamp
    private boolean isCompleted;
    private String category; // e.g., "Shopping", "Exercise", "Eating"

    public Reminder(String title, String description, long dateTime, String category) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.category = category;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
