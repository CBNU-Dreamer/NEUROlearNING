package com.example.neurolearning.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        UserDatabase db = UserDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public void insertUser(User user) {
        new Thread(() -> userDao.insertUser(user)).start();  // 백그라운드에서 삽입
    }

    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username); // UI 쓰레드에서 사용하면 안됨 (추후 LiveData로 바꾸는 것도 가능)
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }
}