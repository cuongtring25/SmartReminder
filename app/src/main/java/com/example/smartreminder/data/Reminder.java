package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "reminders",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class, parentColumns = "category_id", childColumns = "category_id", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = RecurrenceRule.class, parentColumns = "recurrence_id", childColumns = "recurrence_id", onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "user_id", name = "idx_reminders_user_id"),
                @Index(value = "due_date", name = "idx_reminders_due_date"),
                @Index(value = "remind_at", name = "idx_reminders_remind_at"),
                @Index(value = "status", name = "idx_reminders_status"),
                @Index("category_id"),
                @Index("recurrence_id")
        })
public class Reminder {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "reminder_id")
    private String reminderId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "category_id")
    private String categoryId;

    @NonNull
    private String title;

    private String description;

    @NonNull
    @ColumnInfo(name = "due_date")
    private Date dueDate;

    @NonNull
    @ColumnInfo(name = "remind_at")
    private Date remindAt;

    @ColumnInfo(defaultValue = "2")
    private int priority; // 1-Low, 2-Medium, 3-High

    @ColumnInfo(defaultValue = "pending")
    private String status; // pending, done, snoozed, missed

    @ColumnInfo(name = "is_recurring", defaultValue = "0")
    private int isRecurring;

    @ColumnInfo(name = "recurrence_id")
    private String recurrenceId;

    private String location;

    @ColumnInfo(name = "attachment_url")
    private String attachmentUrl;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    private int isDeleted;

    public Reminder(@NonNull String reminderId, @NonNull String userId, @NonNull String title, @NonNull Date dueDate, @NonNull Date remindAt, Date createdAt, Date updatedAt) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
        this.remindAt = remindAt;
        this.createdAt = createdAt != null ? createdAt : new Date();
        this.updatedAt = updatedAt != null ? updatedAt : new Date();
        this.priority = 2;
        this.status = "pending";
        this.isRecurring = 0;
        this.isDeleted = 0;
    }

    // Getters and Setters
    @NonNull public String getReminderId() { return reminderId; }
    public void setReminderId(@NonNull String reminderId) { this.reminderId = reminderId; }

    @NonNull public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @NonNull public Date getDueDate() { return dueDate; }
    public void setDueDate(@NonNull Date dueDate) { this.dueDate = dueDate; }

    @NonNull public Date getRemindAt() { return remindAt; }
    public void setRemindAt(@NonNull Date remindAt) { this.remindAt = remindAt; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIsRecurring() { return isRecurring; }
    public void setIsRecurring(int isRecurring) { this.isRecurring = isRecurring; }

    public String getRecurrenceId() { return recurrenceId; }
    public void setRecurrenceId(String recurrenceId) { this.recurrenceId = recurrenceId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    @NonNull public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }

    public int getIsDeleted() { return isDeleted; }
    public void setIsDeleted(int isDeleted) { this.isDeleted = isDeleted; }
}
