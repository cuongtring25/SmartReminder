package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "badges")
public class Badge {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "badge_id")
    private String badgeId;

    @NonNull
    private String name;

    private String description;

    @ColumnInfo(name = "icon_url")
    private String iconUrl;

    @ColumnInfo(name = "xp_reward")
    private int xpReward;

    public Badge(@NonNull String badgeId, @NonNull String name, String description, int xpReward) {
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
    }

    @NonNull public String getBadgeId() { return badgeId; }
    public void setBadgeId(@NonNull String badgeId) { this.badgeId = badgeId; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
