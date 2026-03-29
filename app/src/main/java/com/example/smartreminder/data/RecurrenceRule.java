package com.example.smartreminder.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "recurrence_rules",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("user_id")})
public class RecurrenceRule {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "recurrence_id")
    private String recurrenceId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    private String frequency; // daily, weekly, monthly, custom

    @ColumnInfo(defaultValue = "1")
    private int interval;

    @ColumnInfo(name = "days_of_week")
    private String daysOfWeek; // JSON: [1,3,5]

    @ColumnInfo(name = "day_of_month")
    private Integer dayOfMonth;

    @NonNull
    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "max_occurrences")
    private Integer maxOccurrences;

    @ColumnInfo(name = "is_active", defaultValue = "1")
    private int isActive;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public RecurrenceRule(@NonNull String recurrenceId, @NonNull String userId, @NonNull String frequency, @NonNull Date startDate) {
        this.recurrenceId = recurrenceId;
        this.userId = userId;
        this.frequency = frequency;
        this.startDate = startDate;
        this.interval = 1;
        this.isActive = 1;
        this.createdAt = new Date();
    }

    // Getters and Setters
    @NonNull public String getRecurrenceId() { return recurrenceId; }
    public void setRecurrenceId(@NonNull String recurrenceId) { this.recurrenceId = recurrenceId; }

    @NonNull public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull public String getFrequency() { return frequency; }
    public void setFrequency(@NonNull String frequency) { this.frequency = frequency; }

    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }

    public Integer getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(Integer dayOfMonth) { this.dayOfMonth = dayOfMonth; }

    @NonNull public Date getStartDate() { return startDate; }
    public void setStartDate(@NonNull Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Integer getMaxOccurrences() { return maxOccurrences; }
    public void setMaxOccurrences(Integer maxOccurrences) { this.maxOccurrences = maxOccurrences; }

    public int getIsActive() { return isActive; }
    public void setIsActive(int isActive) { this.isActive = isActive; }

    @NonNull public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
}
