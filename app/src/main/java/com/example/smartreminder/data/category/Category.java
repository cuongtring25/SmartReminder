package com.example.smartreminder.data.category;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.smartreminder.data.user.User;

import java.util.Date;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private String icon;

    private String color;

    private int is_system = 0;

    private int display_order = 0;

    private int is_deleted = 0;

    @NonNull
    private Date created_at;

    @NonNull
    private Date updated_at;

    public Category(@NonNull String name) {
        this.name = name;
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }



    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getIs_system() { return is_system; }
    public void setIs_system(int is_system) { this.is_system = is_system; }

    public int getDisplay_order() { return display_order; }
    public void setDisplay_order(int display_order) { this.display_order = display_order; }

    public int getIs_deleted() { return is_deleted; }
    public void setIs_deleted(int is_deleted) { this.is_deleted = is_deleted; }

    @NonNull public Date getCreated_at() { return created_at; }
    public void setCreated_at(@NonNull Date created_at) { this.created_at = created_at; }

    @NonNull public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(@NonNull Date updated_at) { this.updated_at = updated_at; }
}
