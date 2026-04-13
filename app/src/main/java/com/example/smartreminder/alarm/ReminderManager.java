package com.example.smartreminder.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import android.app.Activity;

import androidx.core.app.AlarmManagerCompat;

import com.example.smartreminder.data.reminder.Reminder;
import com.example.smartreminder.notification.NotificationHelper;
import com.example.smartreminder.receiver.ReminderReceiver;

import java.util.Date;

public final class ReminderManager {

    private static final String TAG = "ReminderManager";
    private static final int ALARM_KIND_START = 0;
    private static final int ALARM_KIND_END = 1;

    private final Context appContext;

    public ReminderManager(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static boolean canScheduleExactAlarms(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false;
        }
        AlarmManager am = context.getSystemService(AlarmManager.class);
        return am == null || !am.canScheduleExactAlarms();
    }

    public static void openExactAlarmSettings(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        }
    }

    public void cancelReminder(int reminderId) {
        AlarmManager am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }
        PendingIntent startPi = reminderPendingIntent(reminderId, ALARM_KIND_START);
        PendingIntent endPi = reminderPendingIntent(reminderId, ALARM_KIND_END);
        am.cancel(startPi);
        am.cancel(endPi);
        startPi.cancel();
        endPi.cancel();
        // Notification ids are derived from reminderId and kind (start/end).
        NotificationHelper.cancelReminderNotification(appContext, reminderId * 2);
        NotificationHelper.cancelReminderNotification(appContext, reminderId * 2 + 1);
    }

    public boolean scheduleReminder(@NonNull Reminder reminder, boolean userAllowsNotifications) {
        if (reminder.getAlarm_enabled() != 1) {
            return false;
        }
        if (!userAllowsNotifications) {
            Log.d(TAG, "Skipping alarm: user disabled notifications in profile");
            return false;
        }
        if (!"pending".equals(reminder.getStatus()) && !"snoozed".equals(reminder.getStatus())) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && canScheduleExactAlarms(appContext)) {
            Log.w(TAG, "Exact alarm permission not granted");
            return false;
        }

        AlarmManager am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return false;
        }

        boolean scheduledAny = false;

        long startAt = reminder.getRemind_at().getTime();
        if (startAt > now) {
            PendingIntent pi = reminderPendingIntent(reminder.getId(), ALARM_KIND_START);
            AlarmManagerCompat.setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, startAt, pi);
            Log.d(TAG, "Scheduled START alarm id=" + reminder.getId() + " at=" + new Date(startAt));
            scheduledAny = true;
        } else {
            Log.d(TAG, "Skipping start alarm: remind_at is in the past");
        }

        // End alarm (due_date)
        long endAt = reminder.getDue_date().getTime();
        if (endAt > now) {
            PendingIntent pi = reminderPendingIntent(reminder.getId(), ALARM_KIND_END);
            AlarmManagerCompat.setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, endAt, pi);
            Log.d(TAG, "Scheduled END alarm id=" + reminder.getId() + " at=" + new Date(endAt));
            scheduledAny = true;
        } else {
            Log.d(TAG, "Skipping end alarm: due_date is in the past");
        }

        return scheduledAny;
    }

    private PendingIntent reminderPendingIntent(int reminderId, int kind) {
        Intent intent = new Intent(appContext, ReminderReceiver.class);
        if (kind == ALARM_KIND_END) {
            intent.setAction(ReminderReceiver.ACTION_REMINDER_END_FIRE);
        } else {
            intent.setAction(ReminderReceiver.ACTION_REMINDER_START_FIRE);
        }
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId);
        intent.putExtra(ReminderReceiver.EXTRA_REMINDER_KIND, kind);
        intent.setData(Uri.parse("smartreminder://reminder/" + reminderId + "/" + kind));

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        flags |= PendingIntent.FLAG_IMMUTABLE;
        int requestCode = reminderId * 2 + (kind == ALARM_KIND_END ? 1 : 0);
        return PendingIntent.getBroadcast(appContext, requestCode, intent, flags);
    }
}
