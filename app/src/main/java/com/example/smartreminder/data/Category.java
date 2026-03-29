package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "categories",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class Category {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "category_id")
    private String categoryId;

    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String name;

    private String icon;

    private String color;

    @ColumnInfo(name = "is_system", defaultValue = "0")
    private int isSystem;

    @ColumnInfo(name = "display_order", defaultValue = "0")
    private int displayOrder;

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    private int isDeleted;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public Category(@NonNull String categoryId, String userId, @NonNull String name, Date createdAt, Date updatedAt) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt != null ? createdAt : new Date();
        this.updatedAt = updatedAt != null ? updatedAt : new Date();
        this.isSystem = 0;
        this.displayOrder = 0;
        this.isDeleted = 0;
    }

    // Getters and Setters
    @NonNull public String getCategoryId() { return categoryId; }
    public void setCategoryId(@NonNull String categoryId) { this.categoryId = categoryId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getIsSystem() { return isSystem; }
    public void setIsSystem(int isSystem) { this.isSystem = isSystem; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }

    @NonNull public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }
}
