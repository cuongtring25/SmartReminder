package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "reminder_logs",
        foreignKeys = {
                @ForeignKey(entity = Reminder.class, parentColumns = "id", childColumns = "reminder_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "reminder_id", name = "idx_logs_reminder_id"),
                @Index("user_id")
        })
public class ReminderLog {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int reminder_id;

    private int user_id;

    @NonNull
    private String action; // done, snoozed, dismissed

    @NonNull
    private Date actioned_at;

    private Date snooze_until;

    private String note;

    @NonNull
    private Date created_at;

    public ReminderLog(int reminder_id, int user_id, @NonNull String action, @NonNull Date actioned_at) {
        this.reminder_id = reminder_id;
        this.user_id = user_id;
        this.action = action;
        this.actioned_at = actioned_at;
        this.created_at = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReminder_id() { return reminder_id; }
    public void setReminder_id(int reminder_id) { this.reminder_id = reminder_id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    @NonNull public String getAction() { return action; }
    public void setAction(@NonNull String action) { this.action = action; }

    @NonNull public Date getActioned_at() { return actioned_at; }
    public void setActioned_at(@NonNull Date actioned_at) { this.actioned_at = actioned_at; }

    public Date getSnooze_until() { return snooze_until; }
    public void setSnooze_until(Date snooze_until) { this.snooze_until = snooze_until; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @NonNull public Date getCreated_at() { return created_at; }
    public void setCreated_at(@NonNull Date created_at) { this.created_at = created_at; }
}
