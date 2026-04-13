package com.example.smartreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.reminder.Reminder;
import com.example.smartreminder.data.reminder.ReminderDao;
import com.example.smartreminder.data.user.User;
import com.example.smartreminder.data.user.UserDao;
import com.example.smartreminder.notification.NotificationHelper;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderReceiver";

    public static final String ACTION_REMINDER_START_FIRE = "com.example.smartreminder.action.REMINDER_START_FIRE";
    public static final String ACTION_REMINDER_END_FIRE = "com.example.smartreminder.action.REMINDER_END_FIRE";    public static final String EXTRA_REMINDER_ID = "extra_reminder_id";
    public static final String EXTRA_REMINDER_KIND = "extra_reminder_kind";
    public static final int KIND_START = 0;
    public static final int KIND_END = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (!ACTION_REMINDER_START_FIRE.equals(action) && !ACTION_REMINDER_END_FIRE.equals(action)) {
            return;
        }
        int reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, -1);
        if (reminderId < 0) {
            return;
        }
        int kind;
        if (ACTION_REMINDER_END_FIRE.equals(action)) {
            kind = KIND_END;
        } else {
            kind = intent.getIntExtra(EXTRA_REMINDER_KIND, KIND_START);
        }
        final int kindFinal = kind;
        final PendingResult pendingResult = goAsync();
        final Context appCtx = context.getApplicationContext();

        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            try {
                ReminderDatabase db = ReminderDatabase.getDatabase(appCtx);
                ReminderDao reminderDao = db.reminderDao();
                UserDao userDao = db.userDao();

                Reminder reminder = reminderDao.getById(reminderId);
                if (reminder == null) {
                    pendingResult.finish();
                    return;
                }
                if (reminder.getIs_deleted() != 0) {
                    pendingResult.finish();
                    return;
                }
                if (reminder.getAlarm_enabled() != 1) {
                    pendingResult.finish();
                    return;
                }
                if (!"pending".equals(reminder.getStatus()) && !"snoozed".equals(reminder.getStatus())) {
                    pendingResult.finish();
                    return;
                }

                User user = userDao.getById(reminder.getUser_id());
                if (user == null || user.getNotification_enabled() != 1) {
                    pendingResult.finish();
                    return;
                }

                long now = System.currentTimeMillis();
                long expectedAt = (kindFinal == KIND_END ? reminder.getDue_date().getTime() : reminder.getRemind_at().getTime());
                    if (expectedAt > now + 60_000L) {
                        Log.d(TAG, "Ignoring early alarm for reminder " + reminderId + " kind=" + kind);
                        Log.d(TAG, "Ignoring early alarm for reminder " + reminderId + " kind=" + kindFinal);
                        pendingResult.finish();
                        return;
                    }


                final Reminder toShow = reminder;
                Handler main = new Handler(Looper.getMainLooper());
                main.post(() -> {
                    try {
                        NotificationHelper.showReminderNotification(appCtx, toShow, kindFinal);
                    } finally {
                        pendingResult.finish();
                    }
                });
            } catch (Throwable t) {
                Log.e(TAG, "Reminder alarm failed", t);
                pendingResult.finish();
            }
        });
    }
}
