package com.example.smartreminder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements ScheduleAdapter.OnScheduleChangeListener {

    private RecyclerView rvSchedules;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private TextView progressText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvSchedules = view.findViewById(R.id.rvSchedules);
        emptyState = view.findViewById(R.id.emptyState);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);

        setupRecyclerView();
        updateUI();

        return view;
    }

    private void setupRecyclerView() {
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(scheduleList, this);
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

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String name = etActivityName.getText().toString();
                    String start = etStartTime.getText().toString();
                    String end = etEndTime.getText().toString();
                    String location = etLocation.getText().toString();
                    boolean alarm = switchAlarm.isChecked();

                    if (!name.isEmpty() && !start.isEmpty()) {
                        scheduleList.add(new Schedule(name, start, end, location, alarm));
                        Collections.sort(scheduleList, (s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
                        adapter.notifyDataSetChanged();
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> 
                        editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour)),
                hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onScheduleChanged() {
        calculateProgress();
    }

    private void calculateProgress() {
        if (scheduleList.isEmpty()) {
            progressBar.setProgress(0);
            progressText.setText("0%");
            return;
        }

        int completedCount = 0;
        for (Schedule schedule : scheduleList) {
            if (schedule.isCompleted()) {
                completedCount++;
            }
        }

        int progress = (int) ((float) completedCount / scheduleList.size() * 100);
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");

        // Notify activity about progress change for streak logic
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onProgressUpdated(progress);
        }
    }

    private void updateUI() {
        if (scheduleList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);
            calculateProgress();
        }
    }
}
