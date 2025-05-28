package com.example.neurolearning.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                User.class,
                GamePlayRecord.class,
                UserGameStatus.class,
                StoryStatus.class
        },
        version = 3, // 🎯 Foreign Key 제거로 인한 스키마 변경으로 버전 3
        exportSchema = false
)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase instance;

    public abstract UserDao userDao();
    public abstract GameProgressDao gameProgressDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, "user_database")
                    .fallbackToDestructiveMigration() // 기존 DB 완전 삭제하고 새로 생성
                    .build();
        }
        return instance;
    }
}