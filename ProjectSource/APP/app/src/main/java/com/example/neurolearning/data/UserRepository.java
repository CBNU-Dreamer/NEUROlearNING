package com.example.neurolearning.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executor;

    public UserRepository(Application application) {
        UserDatabase db = UserDatabase.getInstance(application);
        userDao = db.userDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // 기존 메서드들
    public void insertUser(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    // 새로 추가된 메서드들
    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    public User getUserByUsernameSync(String username) {
        try {
            Future<User> future = executor.submit(() -> userDao.getUserByUsernameSync(username));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User loginUser(String username, String password) {
        try {
            Future<User> future = executor.submit(() -> userDao.loginUser(username, password));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isUserExists(String username) {
        try {
            Future<Integer> future = executor.submit(() -> userDao.getUserCount(username));
            return future.get() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}