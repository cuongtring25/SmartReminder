package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "reminder_logs",
        foreignKeys = {
                @ForeignKey(entity = Reminder.class, parentColumns = "reminder_id", childColumns = "reminder_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "reminder_id", name = "idx_logs_reminder_id"),
                @Index("user_id")
        })
public class ReminderLog {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "log_id")
    private String logId;

    @NonNull
    @ColumnInfo(name = "reminder_id")
    private String reminderId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String action; // done, snoozed, dismissed

    @NonNull
    @ColumnInfo(name = "actioned_at")
    private Date actionedAt;

    @ColumnInfo(name = "snooze_until")
    private Date snoozeUntil;

    private String note;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public ReminderLog(@NonNull String logId, @NonNull String reminderId, @NonNull String userId, @NonNull String action, @NonNull Date actionedAt) {
        this.logId = logId;
        this.reminderId = reminderId;
        this.userId = userId;
        this.action = action;
        this.actionedAt = actionedAt;
        this.createdAt = new Date();
    }

    // Getters and Setters
    @NonNull public String getLogId() { return logId; }
    public void setLogId(@NonNull String logId) { this.logId = logId; }

    @NonNull public String getReminderId() { return reminderId; }
    public void setReminderId(@NonNull String reminderId) { this.reminderId = reminderId; }

    @NonNull public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull public String getAction() { return action; }
    public void setAction(@NonNull String action) { this.action = action; }

    @NonNull public Date getActionedAt() { return actionedAt; }
    public void setActionedAt(@NonNull Date actionedAt) { this.actionedAt = actionedAt; }

    public Date getSnoozeUntil() { return snoozeUntil; }
    public void setSnoozeUntil(Date snoozeUntil) { this.snoozeUntil = snoozeUntil; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @NonNull public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
}
