package com.example.smartreminder.data.recurrencerule;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.smartreminder.data.user.User;

import java.util.Date;

@Entity(tableName = "recurrence_rules")
public class RecurrenceRule {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String frequency; // daily, weekly, monthly, custom

    private int interval = 1;

    private String days_of_week; // JSON: [1,3,5]

    private Integer day_of_month;

    @NonNull
    private Date start_date;

    private Date end_date;

    private Integer max_occurrences;

    private int is_active = 1;

    @NonNull
    private Date created_at;

    public RecurrenceRule( @NonNull String frequency, @NonNull Date start_date) {
        this.frequency = frequency;
        this.start_date = start_date;
        this.created_at = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    @NonNull public String getFrequency() { return frequency; }
    public void setFrequency(@NonNull String frequency) { this.frequency = frequency; }

    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }

    public String getDays_of_week() { return days_of_week; }
    public void setDays_of_week(String days_of_week) { this.days_of_week = days_of_week; }

    public Integer getDay_of_month() { return day_of_month; }
    public void setDay_of_month(Integer day_of_month) { this.day_of_month = day_of_month; }

    @NonNull public Date getStart_date() { return start_date; }
    public void setStart_date(@NonNull Date start_date) { this.start_date = start_date; }

    public Date getEnd_date() { return end_date; }
    public void setEnd_date(Date end_date) { this.end_date = end_date; }

    public Integer getMax_occurrences() { return max_occurrences; }
    public void setMax_occurrences(Integer max_occurrences) { this.max_occurrences = max_occurrences; }

    public int getIs_active() { return is_active; }
    public void setIs_active(int is_active) { this.is_active = is_active; }

    @NonNull public Date getCreated_at() { return created_at; }
    public void setCreated_at(@NonNull Date created_at) { this.created_at = created_at; }
}
