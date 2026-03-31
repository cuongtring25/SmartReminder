package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "categories",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private Integer user_id;

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

    public Category(Integer user_id, @NonNull String name) {
        this.user_id = user_id;
        this.name = name;
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

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
