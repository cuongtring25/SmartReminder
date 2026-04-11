package com.example.smartreminder;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.category.Category;
import com.example.smartreminder.data.reminder.Reminder;
import com.example.smartreminder.data.reminder.ReminderDao;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements ScheduleAdapter.OnScheduleChangeListener {

    private RecyclerView rvSchedules;
    private ScheduleAdapter adapter;
    private List<Reminder> reminderList;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private TextView progressText;
    
    private ReminderDao reminderDao;
    private int currentUserId;

    private ReminderDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        loadRemindersFromDb();

        return view;
    }

    private void setupRecyclerView() {
        reminderList = new ArrayList<>();
        adapter = new ScheduleAdapter(reminderList, this);
        rvSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedules.setAdapter(adapter);
    }

    public void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null);
        
        EditText etActivityName = view.findViewById(R.id.etActivityName);
        EditText etStartTime = view.findViewById(R.id.etStartTime);
        EditText etEndTime = view.findViewById(R.id.etEndTime);
        EditText etLocation = view.findViewById(R.id.etLocation);
        SwitchMaterial switchAlarm = view.findViewById(R.id.switchAlarm);

        //set up category choice
        final int[] selectedCategoryId = {-1};

        AutoCompleteTextView spinnerCategory = view.findViewById(R.id.spinnerCategory);

        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            //select all category from database
            List<Category> allCategories = db.categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : allCategories) categoryNames.add(c.getName());
            //using spinner to show category list
            getActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, categoryNames);
                spinnerCategory.setAdapter(adapter);

                //  set event listener for spinner item selection
                spinnerCategory.setOnItemClickListener((parent, v, position, id) -> {
                    // taking ID of selected category from database
                    selectedCategoryId[0] = allCategories.get(position).getId();
                });
            });
        });

        //set up time picker for remind time and due time
        final Calendar startCal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime, startCal));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime, endCal));

        //add new reminder
        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String name = etActivityName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();

                    int finalCategoryId = selectedCategoryId[0];

                    if (!name.isEmpty()) {
                        Reminder newReminder = new Reminder(currentUserId,finalCategoryId, name, endCal.getTime(), startCal.getTime());
                        newReminder.setLocation(location);
                        
                        ReminderDatabase.databaseWriteExecutor.execute(() -> {
                            reminderDao.insert(newReminder);
                            loadRemindersFromDb();
                        });
                    } else {
                        Toast.makeText(getContext(), "please fill your activity name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

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
                    // Sort by time
                    Collections.sort(reminderList, (r1, r2) -> r1.getDue_date().compareTo(r2.getDue_date()));
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
        // update the reminder status in the database
        ReminderDatabase.databaseWriteExecutor.execute(() -> {
            reminderDao.update(reminder);
            if (isAdded()) {
                requireActivity().runOnUiThread(this::calculateProgress);
            }
        });
    }

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
