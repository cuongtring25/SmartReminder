package com.example.smartreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smartreminder.alarm.ReminderManager;
import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.reminder.Reminder;
import com.example.smartreminder.data.reminder.ReminderDao;
import com.example.smartreminder.data.user.User;
import com.example.smartreminder.data.user.UserDao;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        final PendingResult pendingResult = goAsync();
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            try {
                ReminderDatabase db = ReminderDatabase.getDatabase(context);
                ReminderDao reminderDao = db.reminderDao();
                UserDao userDao = db.userDao();

                long now = System.currentTimeMillis();
                List<Reminder> toRestore = reminderDao.getFutureAlarmReminders(now);
                ReminderManager manager = new ReminderManager(context);

                for (Reminder reminder : toRestore) {
                    User user = userDao.getById(reminder.getUser_id());
                    boolean allow = user != null && user.getNotification_enabled() == 1;
                    if (!manager.scheduleReminder(reminder, allow)) {
                        Log.d(TAG, "Could not reschedule reminder id=" + reminder.getId());
                    }
                }
            } finally {
                pendingResult.finish();
            }
        });
    }
}
