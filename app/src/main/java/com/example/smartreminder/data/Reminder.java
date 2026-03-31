package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "reminders",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "category_id", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = RecurrenceRule.class, parentColumns = "id", childColumns = "recurrence_id", onDelete = ForeignKey.SET_NULL)
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
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int user_id;

    private Integer category_id;

    @NonNull
    private String title;

    private String description;

    @NonNull
    private Date due_date;

    @NonNull
    private Date remind_at;

    private int priority = 2;

    private String status = "pending";

    private int is_recurring = 0;

    private Integer recurrence_id;

    private String location;

    private String attachment_url;

    @NonNull
    private Date created_at;

    @NonNull
    private Date updated_at;

    private int is_deleted = 0;

    public Reminder(int user_id, @NonNull String title, @NonNull Date due_date, @NonNull Date remind_at) {
        this.user_id = user_id;
        this.title = title;
        this.due_date = due_date;
        this.remind_at = remind_at;
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public Integer getCategory_id() { return category_id; }
    public void setCategory_id(Integer category_id) { this.category_id = category_id; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @NonNull public Date getDue_date() { return due_date; }
    public void setDue_date(@NonNull Date due_date) { this.due_date = due_date; }

    @NonNull public Date getRemind_at() { return remind_at; }
    public void setRemind_at(@NonNull Date remind_at) { this.remind_at = remind_at; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIs_recurring() { return is_recurring; }
    public void setIs_recurring(int is_recurring) { this.is_recurring = is_recurring; }

    public Integer getRecurrence_id() { return recurrence_id; }
    public void setRecurrence_id(Integer recurrence_id) { this.recurrence_id = recurrence_id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAttachment_url() { return attachment_url; }
    public void setAttachment_url(String attachment_url) { this.attachment_url = attachment_url; }

    @NonNull public Date getCreated_at() { return created_at; }
    public void setCreated_at(@NonNull Date created_at) { this.created_at = created_at; }

    @NonNull public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(@NonNull Date updated_at) { this.updated_at = updated_at; }

    public int getIs_deleted() { return is_deleted; }
    public void setIs_deleted(int is_deleted) { this.is_deleted = is_deleted; }
}
