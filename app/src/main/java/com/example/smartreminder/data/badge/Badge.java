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

    private String description;

    private String icon_url;

    private int xp_reward;

    public Badge(@NonNull String name, String description, int xp_reward) {
        this.name = name;
        this.description = description;
        this.xp_reward = xp_reward;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon_url() { return icon_url; }
    public void setIcon_url(String icon_url) { this.icon_url = icon_url; }

    public int getXp_reward() { return xp_reward; }
    public void setXp_reward(int xp_reward) { this.xp_reward = xp_reward; }
}
