package com.example.smartreminder.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.Manifest;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartreminder.MainActivity;
import com.example.smartreminder.R;
import com.example.smartreminder.data.reminder.Reminder;

public final class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    public static final String CHANNEL_REMINDERS_ID = "smart_reminder_channel";
    public static final String CHANNEL_BADGES_ID = "smart_reminder_badges";

    private static final int BADGE_NOTIFICATION_ID_BASE = 500_000;

    private NotificationHelper() {
    }

    public static void ensureChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        if (nm == null) {
            return;
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        NotificationChannel reminders = new NotificationChannel(
                CHANNEL_REMINDERS_ID,
                context.getString(R.string.notification_channel_reminders_title),
                NotificationManager.IMPORTANCE_HIGH);
        reminders.setDescription(context.getString(R.string.notification_channel_reminders_desc));
        reminders.enableLights(true);
        reminders.setLightColor(Color.parseColor("#6750A4"));
        reminders.enableVibration(true);
        reminders.setVibrationPattern(new long[]{0, 400, 200, 400});
        reminders.setSound(soundUri, audioAttributes);
        reminders.setBypassDnd(false);
        nm.createNotificationChannel(reminders);

        NotificationChannel badges = new NotificationChannel(
                CHANNEL_BADGES_ID,
                context.getString(R.string.notification_channel_badges_title),
                NotificationManager.IMPORTANCE_HIGH);
        badges.setDescription(context.getString(R.string.notification_channel_badges_desc));
        badges.enableVibration(true);
        badges.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(badges);
    }

    private static PendingIntent contentIntentForMain(Context context, int requestCode) {
        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        open.putExtra(MainActivity.EXTRA_OPEN_HOME, true);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(context, requestCode, open, flags);
    }


    public static void showReminderNotification(Context context, Reminder reminder, int kind) {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted; dropping reminder notification");
            return;
        }
        ensureChannels(context);
        int notificationId = notificationIdForReminder(reminder.getId(), kind);
        PendingIntent tap = contentIntentForMain(context, notificationId);

        String phaseText = (kind == com.example.smartreminder.receiver.ReminderReceiver.KIND_END)
                ? context.getString(R.string.notification_reminder_end)
                : context.getString(R.string.notification_reminder_start);

        String text = phaseText;
        if (reminder.getLocation() != null && !reminder.getLocation().isEmpty()) {
            text = phaseText + ": " + reminder.getLocation();
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle(reminder.getTitle())
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(tap)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Log.d(TAG, "notify() id=" + notificationId + " reminderId=" + reminder.getId() + " kind=" + kind);
        NotificationManagerCompat.from(context).notify(notificationId, b.build());
    }

    public static void showBadgeEarnedNotification(Context context, int userId, int badgeId,
                                                   String badgeName, String badgeDescription) {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ensureChannels(context);
        int notificationId = BADGE_NOTIFICATION_ID_BASE + userId * 32 + badgeId;
        PendingIntent tap = contentIntentForMain(context, notificationId);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_BADGES_ID)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle(context.getString(R.string.notification_badge_title, badgeName))
                .setContentText(badgeDescription != null ? badgeDescription : "")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(badgeDescription != null ? badgeDescription : ""))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(tap);

        NotificationManagerCompat.from(context).notify(notificationId, b.build());
    }

    public static void cancelReminderNotification(Context context, int reminderId) {
        NotificationManagerCompat.from(context).cancel(reminderId);
    }

    public static int notificationIdForReminder(int reminderId, int kind) {
        return reminderId * 2 + (kind == com.example.smartreminder.receiver.ReminderReceiver.KIND_END ? 1 : 0);
    }
}
