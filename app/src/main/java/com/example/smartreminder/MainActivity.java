package com.example.smartreminder;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private StatsFragment statsFragment;
    private ProfileFragment profileFragment;
    private TextView tvStreakCount;
    private int currentStreak = 4; // Mock value
    private boolean isStreakCountedToday = false;

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

        tvStreakCount = findViewById(R.id.tvStreakCount);
        tvStreakCount.setText(String.valueOf(currentStreak));

        homeFragment = new HomeFragment();
        statsFragment = new StatsFragment();
        profileFragment = new ProfileFragment();

        // Default fragment
        loadFragment(homeFragment);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                loadFragment(homeFragment);
                return true;
            } else if (id == R.id.navigation_stats) {
                loadFragment(statsFragment);
                return true;
            } else if (id == R.id.navigation_profile) {
                loadFragment(profileFragment);
                return true;
            }
            return false;
        });

        ImageButton btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnAddSchedule.setOnClickListener(v -> {
            if (homeFragment.isAdded()) {
                homeFragment.showAddScheduleDialog();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void onProgressUpdated(int progress) {
        if (progress >= 80 && !isStreakCountedToday) {
            currentStreak++;
            isStreakCountedToday = true;
            tvStreakCount.setText(String.valueOf(currentStreak));
        } else if (progress < 80 && isStreakCountedToday) {
            currentStreak--;
            isStreakCountedToday = false;
            tvStreakCount.setText(String.valueOf(currentStreak));
        }
    }
}
