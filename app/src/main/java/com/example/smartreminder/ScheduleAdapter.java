package com.example.smartreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<Schedule> schedules;
    private OnScheduleChangeListener listener;

    public interface OnScheduleChangeListener {
        void onScheduleChanged();
    }

    public ScheduleAdapter(List<Schedule> schedules, OnScheduleChangeListener listener) {
        this.schedules = schedules;
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
        Schedule schedule = schedules.get(position);
        holder.tvActivityName.setText(schedule.getActivityName());
        holder.tvTime.setText(String.format("%s - %s", schedule.getStartTime(), schedule.getEndTime()));
        holder.tvLocation.setText(schedule.getLocation());
        holder.ivAlarm.setVisibility(schedule.isAlarmSet() ? View.VISIBLE : View.GONE);
        
        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(schedule.isCompleted());
        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            schedule.setCompleted(isChecked);
            if (listener != null) {
                listener.onScheduleChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
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
