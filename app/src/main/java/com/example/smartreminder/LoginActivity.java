package com.example.smartreminder;

import static com.example.smartreminder.data.ReminderDatabase.databaseWriteExecutor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartreminder.data.ReminderDatabase;
import com.example.smartreminder.data.badge.BadgeDao;
import com.example.smartreminder.data.user.User;
import com.example.smartreminder.data.user.UserDao;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        ReminderDatabase db = ReminderDatabase.getDatabase(this);
        UserDao userDao = db.userDao();






        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Simple mock login validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "please enter your full information", Toast.LENGTH_SHORT).show();
            } else {
                databaseWriteExecutor.execute(() -> {
                    // find user by email
                    User user = userDao.findByEmail(email);
                    BadgeDao badgeDao = db.badgeDao();
                    badgeDao.updateBadge(1, "@drawable/ic_fire",10);
                    badgeDao.updateBadge(2, "@android:drawable/ic_menu_compass",10);
                    // return to UI THREAD
                    runOnUiThread(() -> {
                        if (user != null) {
                            // check password
                            if (user.getPassword_hash().equals(password)) {
                                
                                // save information into SharedPreferences
                                saveUserSession(user);

                                Toast.makeText(LoginActivity.this, "login success!", Toast.LENGTH_SHORT).show();

                                // Navigate to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "password is not correct", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }

    private void saveUserSession(User user) {
        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        
        editor.putInt("userId", user.getId());
        editor.putString("userName", user.getFull_name());
        editor.putInt("currentStreak", user.getCurrent_streak());
        editor.putInt("longestStreak", user.getLongest_streak());
        editor.putInt("xp", user.getXp());
        editor.putInt("level", user.getLevel());
        
        editor.apply();
    }
}
