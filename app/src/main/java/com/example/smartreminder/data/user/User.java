package com.example.smartreminder.data.user;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String email;

    @NonNull
    private String password_hash;

    @NonNull
    private String full_name;

    private String avatar_url;

    private String phone;

    private String theme = "light";

    private String language = "vi";

    private int notification_enabled = 1;

    private int current_streak = 0;

    private int longest_streak = 0;

    private int xp = 0;

    private int level = 1;

    private Date last_completion_date;

    @NonNull
    private Date created_at;

    @NonNull
    private Date updated_at;

    private int is_deleted = 0;

    public User(@NonNull String email, @NonNull String password_hash, @NonNull String full_name, String phone, String avatar_url) {
        this.email = email;
        this.password_hash = password_hash;
        this.full_name = full_name;
        this.phone = phone;
        this.avatar_url = avatar_url;
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull public String getPassword_hash() { return password_hash; }
    public void setPassword_hash(@NonNull String password_hash) { this.password_hash = password_hash; }

    @NonNull public String getFull_name() { return full_name; }
    public void setFull_name(@NonNull String full_name) { this.full_name = full_name; }

    public String getAvatar_url() { return avatar_url; }
    public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getNotification_enabled() { return notification_enabled; }
    public void setNotification_enabled(int notification_enabled) { this.notification_enabled = notification_enabled; }

    public int getCurrent_streak() { return current_streak; }
    public void setCurrent_streak(int current_streak) { this.current_streak = current_streak; }

    public int getLongest_streak() { return longest_streak; }
    public void setLongest_streak(int longest_streak) { this.longest_streak = longest_streak; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Date getLast_completion_date() { return last_completion_date; }
    public void setLast_completion_date(Date last_completion_date) { this.last_completion_date = last_completion_date; }

    @NonNull public Date getCreated_at() { return created_at; }
    public void setCreated_at(@NonNull Date created_at) { this.created_at = created_at; }

    @NonNull public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(@NonNull Date updated_at) { this.updated_at = updated_at; }

    public int getIs_deleted() { return is_deleted; }
    public void setIs_deleted(int is_deleted) { this.is_deleted = is_deleted; }
}
