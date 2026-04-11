package com.example.smartreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.badge.Badge;
import com.example.smartreminder.data.userbadge.UserBadge;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvLevelValue, tvXPValue, tvXPRequired, tvRemainingXP;
    private ProgressBar levelProgressBar;
    private RecyclerView rvBadges;
    private BadgeAdapter badgeAdapter;
    private List<Badge> userBadges = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvLevelValue = view.findViewById(R.id.tvLevelValue);
        tvXPValue = view.findViewById(R.id.tvXPValue);
        tvXPRequired = view.findViewById(R.id.tvXPRequired);
        tvRemainingXP = view.findViewById(R.id.tvRemainingXP);
        levelProgressBar = view.findViewById(R.id.levelProgressBar);
        rvBadges = view.findViewById(R.id.rvBadges);

        setupRecyclerView();
        loadUserProfile();
        loadUserBadges();

        return view;
    }

    private void setupRecyclerView() {
        badgeAdapter = new BadgeAdapter(userBadges);
        rvBadges.setAdapter(badgeAdapter);
    }

    private void loadUserProfile() {
        SharedPreferences pref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = pref.getString("userName", "User");
        int level = pref.getInt("level", 1);
        int xp = pref.getInt("xp", 0);

        tvProfileName.setText(userName);
        tvLevelValue.setText(String.valueOf(level));
        tvXPValue.setText(xp + " XP");

        // Giả sử mỗi level cần 300 XP
        int maxXP = 300;
        int currentXPInLevel = xp % maxXP;
        int remainingXP = maxXP - currentXPInLevel;

        levelProgressBar.setMax(maxXP);
        levelProgressBar.setProgress(currentXPInLevel);
        tvXPRequired.setText(maxXP + " XP");
        tvRemainingXP.setText(remainingXP + " XP until next level");
    }

    private void loadUserBadges() {
        SharedPreferences pref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            ReminderDatabase db = ReminderDatabase.getDatabase(requireContext());
            ReminderDatabase.databaseWriteExecutor.execute(() -> {
                List<UserBadge> userBadgeLinks = db.userBadgeDao().getBadgesForUser(userId);
                List<Badge> badges = new ArrayList<>();
                for (UserBadge link : userBadgeLinks) {
                    Badge b = db.badgeDao().getById(link.getBadge_id());
                    if (b != null) {
                        badges.add(b);
                    }
                }

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        userBadges.clear();
                        userBadges.addAll(badges);
                        badgeAdapter.notifyDataSetChanged();
                    });
                }
            });
        }
    }
}
