package com.example.smartreminder.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Reminder.class, Category.class, ReminderLog.class, RecurrenceRule.class, Badge.class, UserBadge.class}, version = 4, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ReminderDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ReminderDao reminderDao();
    public abstract CategoryDao categoryDao();
    public abstract ReminderLogDao reminderLogDao();
    public abstract RecurrenceRuleDao recurrenceRuleDao();

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
                UserDao userDao = INSTANCE.userDao();
                ReminderDao reminderDao = INSTANCE.reminderDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();

                // 1. Create System Admin
                String adminId = UUID.randomUUID().toString();
                User admin = new User(adminId, "admin@example.com", "hash_123456", "System Admin", new Date(), new Date());
                admin.setLevel(1);
                admin.setXp(150);
                admin.setCurrentStreak(4);
                userDao.insert(admin);

                // 2. Create Default Categories
                String cat1Id = UUID.randomUUID().toString();
                Category cat1 = new Category(cat1Id, null, "Học tập", new Date(), new Date());
                cat1.setIsSystem(1);
                categoryDao.insert(cat1);

                String cat2Id = UUID.randomUUID().toString();
                Category cat2 = new Category(cat2Id, null, "Cá nhân", new Date(), new Date());
                cat2.setIsSystem(1);
                categoryDao.insert(cat2);

                // 3. Create Demo Reminders for Today (to show 20% completion like in image)
                // We need 5 tasks, 1 completed (1/5 = 20%)
                long today = System.currentTimeMillis();
                
                // Completed Task
                Reminder task1 = new Reminder(UUID.randomUUID().toString(), adminId, "Study Android", new Date(today), new Date(today), new Date(), new Date());
                task1.setStatus("done");
                task1.setCategoryId(cat1Id);
                reminderDao.insert(task1);

                // Pending Tasks
                reminderDao.insert(new Reminder(UUID.randomUUID().toString(), adminId, "Buy groceries", new Date(today), new Date(today), new Date(), new Date()));
                reminderDao.insert(new Reminder(UUID.randomUUID().toString(), adminId, "Team meeting", new Date(today), new Date(today), new Date(), new Date()));
                reminderDao.insert(new Reminder(UUID.randomUUID().toString(), adminId, "Gym session", new Date(today), new Date(today), new Date(), new Date()));
                reminderDao.insert(new Reminder(UUID.randomUUID().toString(), adminId, "Read a book", new Date(today), new Date(today), new Date(), new Date()));
            });
        }
    };
}
