package com.example.smartreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleChangeListener {

    private RecyclerView rvSchedules;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList;
    private LinearLayout emptyState;
    private CardView progressCard;
    private ProgressBar progressBar;
    private TextView progressText;
    private MaterialToolbar toolbar;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        updateGreeting();
        updateUI();

        ImageButton btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnAddSchedule.setOnClickListener(v -> showAddScheduleDialog());

        ImageButton btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(v -> showDatePicker());
    }

    private void initViews() {
        rvSchedules = findViewById(R.id.rvSchedules);
        emptyState = findViewById(R.id.emptyState);
        progressCard = findViewById(R.id.progressCard);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        toolbar = findViewById(R.id.toolbar);
    }

    private void updateGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (timeOfDay < 12) greeting = "Chào buổi sáng!";
        else if (timeOfDay < 18) greeting = "Chào buổi chiều!";
        else greeting = "Chào buổi tối!";

        toolbar.setSubtitle(greeting);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(selectedDate.getTime());
                    
                    TextView tvProgressTitle = findViewById(R.id.tvProgressTitle);
                    if (tvProgressTitle != null) {
                        tvProgressTitle.setText("Tiến độ ngày " + formattedDate);
                    }
                    Toast.makeText(this, "Đã chọn ngày: " + formattedDate, Toast.LENGTH_SHORT).show();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void setupRecyclerView() {
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(scheduleList, this);
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(adapter);
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
        
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
                        sortSchedules();
                        adapter.notifyDataSetChanged();
                        updateUI();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập tên và giờ bắt đầu", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> 
                        editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void sortSchedules() {
        Collections.sort(scheduleList, (s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
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
    }

    @Override
    public void onScheduleChanged() {
        calculateProgress();
    }

    private void updateUI() {
        if (scheduleList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
            progressCard.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);
            progressCard.setVisibility(View.VISIBLE);
            calculateProgress();
        }
    }
}
