package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String email;

    @NonNull
    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    @NonNull
    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    private String phone;

    @ColumnInfo(defaultValue = "light")
    private String theme;

    @ColumnInfo(defaultValue = "vi")
    private String language;

    @ColumnInfo(name = "notification_enabled", defaultValue = "1")
    private int notificationEnabled;

    // Gamification Fields
    @ColumnInfo(name = "current_streak", defaultValue = "0")
    private int currentStreak;

    @ColumnInfo(name = "longest_streak", defaultValue = "0")
    private int longestStreak;

    @ColumnInfo(defaultValue = "0")
    private int xp;

    @ColumnInfo(defaultValue = "1")
    private int level;

    @ColumnInfo(name = "last_completion_date")
    private Date lastCompletionDate;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    private int isDeleted;

    public User(@NonNull String userId, @NonNull String email, @NonNull String passwordHash, @NonNull String fullName, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt != null ? createdAt : new Date();
        this.updatedAt = updatedAt != null ? updatedAt : new Date();
        this.theme = "light";
        this.language = "vi";
        this.notificationEnabled = 1;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.xp = 0;
        this.level = 1;
        this.isDeleted = 0;
    }

    // Getters and Setters
    @NonNull public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(@NonNull String passwordHash) { this.passwordHash = passwordHash; }

    @NonNull public String getFullName() { return fullName; }
    public void setFullName(@NonNull String fullName) { this.fullName = fullName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(int notificationEnabled) { this.notificationEnabled = notificationEnabled; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Date getLastCompletionDate() { return lastCompletionDate; }
    public void setLastCompletionDate(Date lastCompletionDate) { this.lastCompletionDate = lastCompletionDate; }

    @NonNull public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}
