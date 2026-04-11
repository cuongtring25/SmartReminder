package com.example.smartreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartreminder.data.badge.Badge;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private List<Badge> badges;
    private Context context;

    public BadgeAdapter(List<Badge> badges) {
        this.badges = badges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.tvBadgeName.setText(badge.getName());
        
        // Handle icon
        String iconUrl = badge.getIcon_url();
        if (iconUrl != null) {
            if (iconUrl.startsWith("@drawable/")) {
                int resId = context.getResources().getIdentifier(iconUrl.replace("@drawable/", ""), "drawable", context.getPackageName());
                if (resId != 0) holder.ivBadgeIcon.setImageResource(resId);
            } else if (iconUrl.startsWith("@android:drawable/")) {
                int resId = context.getResources().getIdentifier(iconUrl.replace("@android:drawable/", ""), "drawable", "android");
                if (resId != 0) holder.ivBadgeIcon.setImageResource(resId);
            }
        }
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBadgeIcon;
        TextView tvBadgeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBadgeIcon = itemView.findViewById(R.id.ivBadgeIcon);
            tvBadgeName = itemView.findViewById(R.id.tvBadgeName);
        }
    }
}
