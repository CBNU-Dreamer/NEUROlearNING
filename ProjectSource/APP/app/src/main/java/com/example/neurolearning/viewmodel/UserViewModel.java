package com.example.neurolearning.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserViewModel extends AndroidViewModel {

    private final UserDatabase db;
    private final ExecutorService executor;

    public UserViewModel(@NonNull Application application) {
        super(application);
        db = UserDatabase.getInstance(application);
        executor = Executors.newFixedThreadPool(2);
    }

    // 기존 메서드들
    public void insertUser(User user) {
        executor.execute(() -> db.userDao().insertUser(user));
    }

    public LiveData<List<User>> getAllUsers() {
        return db.userDao().getAllUsers();
    }

    public LiveData<User> getUserByUsername(String username) {
        return db.userDao().getUserByUsername(username);
    }

    // UserInfoActivity에서 사용할 새로운 메서드들
    public User getUserById(String userId) {
        try {
            Future<User> future = executor.submit(() -> db.userDao().getUserByUsernameSync(userId));
            return future.get(); // 동기적으로 결과 대기
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUser(User user) {
        executor.execute(() -> db.userDao().updateUser(user));
    }

    // 로그인 검증용 메서드
    public User loginUser(String username, String password) {
        try {
            Future<User> future = executor.submit(() -> db.userDao().loginUser(username, password));
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 사용자 존재 여부 확인
    public boolean isUserExists(String username) {
        try {
            Future<Integer> future = executor.submit(() -> db.userDao().getUserCount(username));
            return future.get() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}