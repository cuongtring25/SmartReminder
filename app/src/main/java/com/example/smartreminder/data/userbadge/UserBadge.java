package com.example.smartreminder.data.userbadge;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.smartreminder.data.badge.Badge;
import com.example.smartreminder.data.user.User;

import java.util.Date;

@Entity(tableName = "user_badges",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Badge.class, parentColumns = "id", childColumns = "badge_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("badge_id")})
public class UserBadge {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int user_id;

    private int badge_id;

    @NonNull
    private Date earned_at;

    public UserBadge(int user_id, int badge_id, @NonNull Date earned_at) {
        this.user_id = user_id;
        this.badge_id = badge_id;
        this.earned_at = earned_at;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getBadge_id() { return badge_id; }
    public void setBadge_id(int badge_id) { this.badge_id = badge_id; }

    @NonNull public Date getEarned_at() { return earned_at; }
    public void setEarned_at(@NonNull Date earned_at) { this.earned_at = earned_at; }
}
