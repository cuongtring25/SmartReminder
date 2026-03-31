package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import java.util.Date;

@Entity(tableName = "user_badges",
        primaryKeys = {"user_id", "badge_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Badge.class, parentColumns = "badge_id", childColumns = "badge_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("badge_id")})
public class UserBadge {
    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "badge_id")
    private String badgeId;

    @NonNull
    @ColumnInfo(name = "earned_at")
    private Date earnedAt;

    public UserBadge(@NonNull String userId, @NonNull String badgeId, @NonNull Date earnedAt) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.earnedAt = earnedAt;
    }

    @NonNull public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull public String getBadgeId() { return badgeId; }
    public void setBadgeId(@NonNull String badgeId) { this.badgeId = badgeId; }

    @NonNull public Date getEarnedAt() { return earnedAt; }
    public void setEarnedAt(@NonNull Date earnedAt) { this.earnedAt = earnedAt; }
}
