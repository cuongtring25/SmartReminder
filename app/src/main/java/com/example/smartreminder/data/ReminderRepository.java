package com.example.smartreminder.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ReminderRepository {

    private final ReminderDao mReminderDao;
    private final LiveData<List<Reminder>> mAllReminders;

    public ReminderRepository(Application application) {
        ReminderDatabase db = ReminderDatabase.getDatabase(application);
        mReminderDao = db.reminderDao();
        mAllReminders = mReminderDao.getAllReminders();
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return mAllReminders;
    }

    public LiveData<List<Reminder>> getRemindersByCategory(String category) {
        return mReminderDao.getRemindersByCategory(category);
    }

    public void insert(Reminder reminder) {
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            mReminderDao.insert(reminder);
        });
    }

    public void update(Reminder reminder) {
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            mReminderDao.update(reminder);
        });
    }

    public void delete(Reminder reminder) {
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            mReminderDao.delete(reminder);
        });
    }
}
