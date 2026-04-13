package com.example.smartreminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartreminder.alarm.ReminderManager;
import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.badge.Badge;
import com.example.smartreminder.data.category.Category;
import com.example.smartreminder.data.reminder.Reminder;
import com.example.smartreminder.data.reminder.ReminderDao;
import com.example.smartreminder.data.user.User;
import com.example.smartreminder.data.userbadge.UserBadge;
import com.example.smartreminder.notification.NotificationHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class HomeFragment extends Fragment implements ScheduleAdapter.OnScheduleChangeListener {

    private static final String DAY_KEY_FORMAT = "yyyy-MM-dd";

    private RecyclerView rvSchedules;
    private ScheduleAdapter adapter;
    private List<Reminder> reminderList;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private TextView progressText;

    private ReminderDao reminderDao;
    private int currentUserId;

    private ReminderDatabase db;

    private int pendingReminderIdForPermission = -1;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (pendingReminderIdForPermission < 0) {
                        return;
                    }
                    int reminderId = pendingReminderIdForPermission;
                    pendingReminderIdForPermission = -1;
                    if (!granted) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), R.string.toast_notifications_denied, Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                    Context appForAlarm = getContext() != null ? getContext().getApplicationContext() : null;
                    if (appForAlarm == null) {
                        return;
                    }
                    ReminderDatabase.databaseWriteExecutor.execute(() -> {
                        Reminder r = reminderDao.getById(reminderId);
                        if (r == null) {
                            return;
                        }
                        User u = db.userDao().getById(currentUserId);
                        boolean allow = u != null && u.getNotification_enabled() == 1;
                        ReminderManager mgr = new ReminderManager(appForAlarm);
                        boolean ok = mgr.scheduleReminder(r, allow);
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                if (!ok && r.getAlarm_enabled() == 1 && allow) {
                                    maybeShowScheduleFailureToast(r);
                                }
                            });
                        }
                    });
                });

        reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao();
        SharedPreferences pref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);
        db = ReminderDatabase.getDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvSchedules = view.findViewById(R.id.rvSchedules);
        emptyState = view.findViewById(R.id.emptyState);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);

        setupRecyclerView();
        attachSwipeToDelete();
        loadRemindersFromDb();

        return view;
    }

    private void setupRecyclerView() {
        reminderList = new ArrayList<>();
        adapter = new ScheduleAdapter(reminderList, this);
        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(adapter);
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                if (position >= reminderList.size()) {
                    adapter.notifyItemChanged(position);
                    return;
                }
                Reminder removed = reminderList.get(position);
                final Context appCtx = requireContext().getApplicationContext();
                ReminderDatabase.databaseWriteExecutor.execute(() -> {
                    new ReminderManager(appCtx).cancelReminder(removed.getId());
                    reminderDao.delete(removed);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(HomeFragment.this::loadRemindersFromDb);
                    }
                });
            }
        });
        helper.attachToRecyclerView(rvSchedules);
    }

    public void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);

        EditText etActivityName = dialogView.findViewById(R.id.etActivityName);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        SwitchMaterial switchAlarm = dialogView.findViewById(R.id.switchAlarm);

        final int[] selectedCategoryId = {-1};

        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            List<Category> allCategories = db.categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : allCategories) {
                categoryNames.add(c.getName());
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, categoryNames);
                    spinnerCategory.setAdapter(catAdapter);

                    spinnerCategory.setOnItemClickListener((parent, v, position, id) -> selectedCategoryId[0] = allCategories.get(position).getId());
                });
            }
        });

        final Calendar startCal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime, startCal));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime, endCal));

        builder.setView(dialogView)
                .setTitle(R.string.add_new_schedule)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etActivityName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            Integer finalCategoryId = selectedCategoryId[0] >= 0 ? selectedCategoryId[0] : null;

            if (name.isEmpty()) {
                Toast.makeText(getContext(), R.string.toast_schedule_name_required, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean alarmOn = switchAlarm.isChecked();
            Reminder newReminder = new Reminder(currentUserId, finalCategoryId, name,
                    endCal.getTime(), startCal.getTime());
            newReminder.setLocation(location);
            newReminder.setAlarm_enabled(alarmOn ? 1 : 0);

            final Context appCtx = requireContext().getApplicationContext();
            ReminderDatabase.databaseWriteExecutor.execute(() -> {
                long rowId = reminderDao.insert(newReminder);
                newReminder.setId((int) rowId);

                User user = db.userDao().getById(currentUserId);
                boolean userAllowsNotifications = user != null && user.getNotification_enabled() == 1;
                ReminderManager manager = new ReminderManager(appCtx);

                if (newReminder.getAlarm_enabled() == 1 && userAllowsNotifications) {
                    boolean needPostPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            && ContextCompat.checkSelfPermission(appCtx, Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED;
                    if (needPostPermission) {
                        pendingReminderIdForPermission = newReminder.getId();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS));
                        }
                    } else {
                        boolean scheduled = manager.scheduleReminder(newReminder, true);
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                if (!scheduled) {
                                    maybeShowScheduleFailureToast(newReminder);
                                }
                            });
                        }
                    }
                }

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        loadRemindersFromDb();
                        dialog.dismiss();
                    });
                }
            });
        }));

        dialog.show();
    }

    private void maybeShowScheduleFailureToast(@NonNull Reminder reminder) {
        long now = System.currentTimeMillis();
        long startAt = reminder.getRemind_at().getTime();
        long endAt = reminder.getDue_date().getTime();

        boolean startInPast = startAt > 0L && startAt <= now;
        boolean endInPast = endAt > 0L && endAt <= now;

        if (startInPast && endInPast) {
            Toast.makeText(getContext(), R.string.toast_past_reminder_time, Toast.LENGTH_LONG).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && ReminderManager.canScheduleExactAlarms(requireContext())) {
            Toast.makeText(getContext(), R.string.toast_exact_alarm_denied, Toast.LENGTH_LONG).show();
            ReminderManager.openExactAlarmSettings(requireActivity());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadRemindersFromDb() {
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            long now = System.currentTimeMillis();

            // automate snooze past pending reminders
            reminderDao.snoozePastPendingReminders(currentUserId, now);

            // taking today's reminders from DB
            List<Reminder> list = reminderDao.getTodayReminders(currentUserId, now);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    reminderList.clear();
                    reminderList.addAll(list);
                    reminderList.sort(Comparator.comparing(Reminder::getDue_date));
                    adapter.notifyDataSetChanged();
                    updateUI();
                });
            }
        });
    }

    private void showTimePicker(EditText editText, Calendar targetCal) {
        int hour = targetCal.get(Calendar.HOUR_OF_DAY);
        int minute = targetCal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    targetCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    targetCal.set(Calendar.MINUTE, minuteOfHour);
                    editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour));
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onScheduleChanged(Reminder reminder) {
        final Context appCtx = requireContext().getApplicationContext();
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            reminderDao.update(reminder);

            User u = db.userDao().getById(currentUserId);
            boolean userAllows = u != null && u.getNotification_enabled() == 1;
            ReminderManager mgr = new ReminderManager(appCtx);

            if ("completed".equals(reminder.getStatus())) {
                mgr.cancelReminder(reminder.getId());
            } else if (reminder.getAlarm_enabled() == 1) {
                mgr.scheduleReminder(reminder, userAllows);
            } else {
                mgr.cancelReminder(reminder.getId());
            }

            syncUserStreakFromCompletedReminders();

            if ("completed".equals(reminder.getStatus())) {
                tryAwardFirstTaskBadge(appCtx);
                tryAwardStreakBadges(appCtx);
            }

            if (isAdded()) {
                requireActivity().runOnUiThread(this::calculateProgress);
            }
        });
    }
    private void tryAwardFirstTaskBadge(Context appCtx) {
        int completed = reminderDao.countCompletedReminders(currentUserId);
        if (completed != 1) {
            return;
        }
        List<Badge> badges = db.badgeDao().getAllBadges();
        Badge newGuy = null;
        for (Badge b : badges) {
            if ("new guy".equalsIgnoreCase(b.getName())) {
                newGuy = b;
                break;
            }
        }
        if (newGuy == null) {
            return;
        }

        SharedPreferences prefs = appCtx.getSharedPreferences("SmartReminderPrefs", Context.MODE_PRIVATE);
        String prefKey = "first_task_badge_notified_" + currentUserId;
        if (prefs.getBoolean(prefKey, false)) {
            return;
        }

        if (db.userBadgeDao().countUserBadge(currentUserId, newGuy.getId()) == 0) {
            db.userBadgeDao().insert(new UserBadge(currentUserId, newGuy.getId(), new Date()));
        }

        Handler main = new Handler(Looper.getMainLooper());
        Badge badgeForUi = newGuy;
        main.post(() -> {
            showBadgeEarnedNotificationOnMainThread(appCtx, badgeForUi);
            prefs.edit().putBoolean(prefKey, true).apply();
        });
    }

    private void tryAwardStreakBadges(Context appCtx) {
        User user = db.userDao().getById(currentUserId);
        if (user == null) {
            return;
        }
        int streak = user.getCurrent_streak();
        List<Badge> badges = db.badgeDao().getAllBadges();
        Handler main = new Handler(Looper.getMainLooper());
        for (Badge badge : badges) {
            int required = badge.getDay_streak_required();
            if (required <= 0) {
                continue;
            }
            if (streak < required) {
                continue;
            }
            if (db.userBadgeDao().countUserBadge(currentUserId, badge.getId()) > 0) {
                continue;
            }
            db.userBadgeDao().insert(new UserBadge(currentUserId, badge.getId(), new Date()));
            main.post(() -> showBadgeEarnedNotificationOnMainThread(appCtx, badge));
        }
    }

    private void showBadgeEarnedNotificationOnMainThread(Context appCtx, Badge badge) {
        NotificationHelper.showBadgeEarnedNotification(appCtx, currentUserId,
                badge.getId(), badge.getName(), badge.getDescription());
    }


    private void syncUserStreakFromCompletedReminders() {
        User user = db.userDao().getById(currentUserId);
        if (user == null) {
            return;
        }
        List<String> dayKeys = reminderDao.getCompletedLocalDayKeys(currentUserId);
        if (dayKeys.isEmpty()) {
            user.setCurrent_streak(0);
            user.setLast_completion_date(null);
            user.setUpdated_at(new Date());
            db.userDao().update(user);
            return;
        }
        Set<String> daySet = new HashSet<>(dayKeys);
        Calendar today = Calendar.getInstance();
        truncateToStartOfDay(today);
        int streak = computeCalendarStreak(daySet, today);
        user.setCurrent_streak(streak);
        user.setLongest_streak(Math.max(user.getLongest_streak(), streak));
        String lastKey = dayKeys.get(dayKeys.size() - 1);
        try {
            user.setLast_completion_date(parseLocalDayStart(lastKey));
        } catch (ParseException ignored) {
            user.setLast_completion_date(null);
        }
        user.setUpdated_at(new Date());
        db.userDao().update(user);
    }

    private static int computeCalendarStreak(Set<String> completedDayKeys, Calendar todayStart) {
        SimpleDateFormat fmt = new SimpleDateFormat(DAY_KEY_FORMAT, Locale.US);
        fmt.setTimeZone(TimeZone.getDefault());
        String todayStr = fmt.format(todayStart.getTime());
        Calendar yesterday = (Calendar) todayStart.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        String yesterdayStr = fmt.format(yesterday.getTime());

        Calendar anchor;
        if (completedDayKeys.contains(todayStr)) {
            anchor = todayStart;
        } else if (completedDayKeys.contains(yesterdayStr)) {
            anchor = yesterday;
        } else {
            return 0;
        }

        int streak = 0;
        Calendar walk = (Calendar) anchor.clone();
        while (true) {
            String key = fmt.format(walk.getTime());
            if (!completedDayKeys.contains(key)) {
                break;
            }
            streak++;
            walk.add(Calendar.DAY_OF_MONTH, -1);
        }
        return streak;
    }

    private static void truncateToStartOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private static Date parseLocalDayStart(String ymd) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat(DAY_KEY_FORMAT, Locale.US);
        fmt.setTimeZone(TimeZone.getDefault());
        return fmt.parse(ymd);
    }

    @SuppressLint("SetTextI18n")
    private void calculateProgress() {
        if (reminderList.isEmpty()) {
            progressBar.setProgress(0);
            progressText.setText("0%");
            return;
        }

        int completedCount = 0;
        for (Reminder reminder : reminderList) {
            if ("completed".equals(reminder.getStatus())) {
                completedCount++;
            }
        }

        int progress = (int) ((float) completedCount / reminderList.size() * 100);
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onProgressUpdated(progress);
        }
    }

    private void updateUI() {
        if (reminderList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);
            calculateProgress();
        }
    }
}
