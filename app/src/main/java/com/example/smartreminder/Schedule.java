package com.example.smartreminder;

public class Schedule {
    private String activityName;
    private String startTime;
    private String endTime;
    private String location;
    private boolean isAlarmSet;
    private boolean isCompleted;

    public Schedule(String activityName, String startTime, String endTime, String location, boolean isAlarmSet) {
        this.activityName = activityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.isAlarmSet = isAlarmSet;
        this.isCompleted = false;
    }

    public String getActivityName() { return activityName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public boolean isAlarmSet() { return isAlarmSet; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
