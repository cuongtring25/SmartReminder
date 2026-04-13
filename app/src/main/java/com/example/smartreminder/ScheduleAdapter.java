package com.example.smartreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartreminder.data.reminder.Reminder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final List<Reminder> reminders;
    private final OnScheduleChangeListener listener;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnScheduleChangeListener {
        void onScheduleChanged(Reminder reminder);
    }

    public ScheduleAdapter(List<Reminder> reminders, OnScheduleChangeListener listener) {
        this.reminders = reminders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.tvActivityName.setText(reminder.getTitle());
        
        String startTime = timeFormat.format(reminder.getRemind_at());
        String endTime = timeFormat.format(reminder.getDue_date());
        holder.tvTime.setText(String.format("%s - %s", startTime, endTime));
        
        holder.tvLocation.setText(reminder.getLocation() != null ? reminder.getLocation() : "");

        holder.ivAlarm.setVisibility(reminder.getAlarm_enabled() == 1 ? View.VISIBLE : View.GONE);
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(reminder.getStatus().equals("completed"));
        
        // update UI when status is changed
        if ("completed".equals(reminder.getStatus())) {
            holder.tvActivityName.setAlpha(0.5f);
        } else {
            holder.tvActivityName.setAlpha(1.0f);
        }

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.setStatus(isChecked ? "completed" : "pending");
            if (listener != null) {
                listener.onScheduleChanged(reminder);
            }
            // update alpha right away
            holder.tvActivityName.setAlpha(isChecked ? 0.5f : 1.0f);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName, tvTime, tvLocation;
        ImageView ivAlarm;
        CheckBox cbCompleted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivAlarm = itemView.findViewById(R.id.ivAlarm);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
        }
    }
}
