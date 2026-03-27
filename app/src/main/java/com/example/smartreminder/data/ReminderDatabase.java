package com.example.smartreminder.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Reminder.class, User.class}, version = 2, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    public abstract ReminderDao reminderDao();
    public abstract UserDao userDao();

    private static volatile ReminderDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ReminderDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReminderDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ReminderDatabase.class, "reminder_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                ReminderDao dao = INSTANCE.reminderDao();
                UserDao userDao = INSTANCE.userDao();
                
                // Add a dummy user
                User defaultUser = new User("admin", "admin@example.com", "123456");
                userDao.insert(defaultUser);

                Reminder reminder = new Reminder("Chào mừng", "Bắt đầu thêm các lời nhắc mới!", System.currentTimeMillis(), "General");
                dao.insert(reminder);
            });
        }
    };
}
