package com.example.neurolearning.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                User.class,
                GamePlayRecord.class  // 🎯 게임 기록만 별도 테이블로 관리
        },
        version = 5, // 🎯 새로운 User.java 구조에 맞춰 버전 업데이트
        exportSchema = false
)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase instance;

    public abstract UserDao userDao();
    public abstract GameRecordDao gameRecordDao(); // 🎯 게임 기록만 관리하는 DAO

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