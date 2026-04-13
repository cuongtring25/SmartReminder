package com.example.smartreminder.data.badge;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "badges")
public class Badge {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private final String description;

    private String icon_url;

    private final int xp_reward;

    private final int day_streak_required;

    public Badge(@NonNull String name, String description, int xp_reward, int day_streak_required) {
        this.name = name;
        this.description = description;
        this.xp_reward = xp_reward;
        this.day_streak_required = day_streak_required;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }

    public String getIcon_url() { return icon_url; }
    public void setIcon_url(String icon_url) { this.icon_url = icon_url; }

    public int getXp_reward() { return xp_reward; }

    public int getDay_streak_required() { return day_streak_required; }
}
