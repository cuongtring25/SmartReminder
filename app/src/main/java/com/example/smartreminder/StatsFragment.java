package com.example.smartreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.reminder.DayStat;
import com.example.smartreminder.data.reminder.ReminderDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvStreakValue, tvTotalCompleted, tvTodayMissed, 
            tvWeeklyTotal, tvWeeklyCompleted, tvWeeklyMissed, tvChartDetail;
    private LinearLayout chartContainer;
    private ReminderDao reminderDao;
    private int currentUserId;
    private View selectedBar = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao();
        SharedPreferences pref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        tvStreakValue = view.findViewById(R.id.tvStreakValue);
        tvTotalCompleted = view.findViewById(R.id.tvTotalCompleted);
        tvTodayMissed = view.findViewById(R.id.tvTodayMissed);
        tvWeeklyTotal = view.findViewById(R.id.tvWeeklyTotal);
        tvWeeklyCompleted = view.findViewById(R.id.tvWeeklyCompleted);
        tvWeeklyMissed = view.findViewById(R.id.tvWeeklyMissed);
        tvChartDetail = view.findViewById(R.id.tvChartDetail);
        chartContainer = view.findViewById(R.id.chartContainer);

        loadStats();

        return view;
    }

    private void loadStats() {
        SharedPreferences pref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int currentStreak = pref.getInt("currentStreak", 0);
        tvStreakValue.setText(String.valueOf(currentStreak));

        long now = System.currentTimeMillis();

        //  calculate this weekly day: start from which to which
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startOfWeek = cal.getTimeInMillis();
        
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long endOfWeek = cal.getTimeInMillis();

        // taking total completed count from DB
        reminderDao.getTotalCompletedCount(currentUserId).observe(getViewLifecycleOwner(), total -> {
            if (total != null) tvTotalCompleted.setText(String.valueOf(total));
        });

        // taking today's missed count from DB
        reminderDao.getTodayTasksCount(currentUserId, now).observe(getViewLifecycleOwner(), totalToday -> {
            if (totalToday != null) {
                reminderDao.getTodayCompletedCount(currentUserId, now).observe(getViewLifecycleOwner(), completedToday -> {
                    if (completedToday != null) {
                        tvTodayMissed.setText(String.valueOf(totalToday - completedToday));
                    }
                });
            }
        });

        // taking weekly missed count from DB
        reminderDao.getRangeTasksCount(currentUserId, startOfWeek, endOfWeek).observe(getViewLifecycleOwner(), weeklyTotal -> {
            if (weeklyTotal != null) {
                tvWeeklyTotal.setText(String.valueOf(weeklyTotal));
                reminderDao.getRangeCompletedCount(currentUserId, startOfWeek, endOfWeek).observe(getViewLifecycleOwner(), weeklyCompleted -> {
                    if (weeklyCompleted != null) {
                        tvWeeklyCompleted.setText(String.valueOf(weeklyCompleted));
                        tvWeeklyMissed.setText(String.valueOf(weeklyTotal - weeklyCompleted));
                    }
                });
            }
        });

        // Load data for Chart
        reminderDao.getWeeklyCompletedStats(currentUserId, startOfWeek, endOfWeek).observe(getViewLifecycleOwner(), this::updateChart);
    }

    private void updateChart(List<DayStat> stats) {
        chartContainer.removeAllViews();
        tvChartDetail.setText(R.string.select_bar_details);
        selectedBar = null;
        
        int maxCount = 0;
        if (stats != null) {
            for (DayStat stat : stats) {
                if (stat.completedCount > maxCount) maxCount = stat.completedCount;
            }
        }
        if (maxCount == 0) maxCount = 1;

        // setting up format for comparision
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat displayFmt = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        // column statistic
        float density = getResources().getDisplayMetrics().density;
        int maxHeightPx = (int) (140 * density);

        for (int i = 0; i < 7; i++) {
            String dateKey = sdf.format(cal.getTime());
            String displayDate = displayFmt.format(cal.getTime());
            int count = 0;

            // find if there are reminders for this date
            if (stats != null) {
                for (DayStat stat : stats) {
                    if (dateKey.equals(stat.date)) {
                        count = stat.completedCount;
                        break;
                    }
                }
            }

            View bar = new View(getContext());
            int heightPx = (count * maxHeightPx) / maxCount;
            
            // if there are no tasks, set height to 2dp for blurring ,otherwise at least 5 dp
            if (count == 0) {
                heightPx = (int) (4 * density);
            } else if (heightPx < (10 * density)) {
                heightPx = (int) (10 * density);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, heightPx);
            params.weight = 1;
            params.setMargins((int)(6 * density), 0, (int)(6 * density), 0);
            bar.setLayoutParams(params);
            
            int colorRes = (count > 0) ? R.color.red : R.color.progress_background;
            bar.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes));
            bar.setAlpha(0.8f);

            final int finalCount = count;
            final String finalDate = displayDate;
            final int finalColor = ContextCompat.getColor(requireContext(), colorRes);

            bar.setOnClickListener(v -> {
                // Reset previously selected bar
                if (selectedBar != null) {
                    selectedBar.setAlpha(0.8f);
                    selectedBar.setScaleX(1.0f);
                }
                
                // Highlight current bar
                v.setAlpha(1.0f);
                v.setScaleX(1.1f);
                selectedBar = v;

                tvChartDetail.setText(getString(R.string.chart_detail_format, finalDate, finalCount));
            });
            
            chartContainer.addView(bar);
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
    }
}
