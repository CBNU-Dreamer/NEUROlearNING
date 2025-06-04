package com.example.neurolearning.data;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserDao userDao;
    private final ExecutorService executor;

    public UserRepository(Application application) {
        UserDatabase db = UserDatabase.getInstance(application);
        userDao = db.userDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // 🎯 새로운 사용자 가입
    public void insertUser(User user) {
        executor.execute(() -> {
            try {
                userDao.insertUser(user);
                Log.d(TAG, "✅ 사용자 가입 성공: " + user.getName());
            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 가입 실패", e);
            }
        });
    }

    // 🎯 사용자 정보 업데이트
    public void updateUser(User user) {
        executor.execute(() -> {
            try {
                userDao.updateUser(user);
                Log.d(TAG, "✅ 사용자 정보 업데이트 성공: " + user.getName());
            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 정보 업데이트 실패", e);
            }
        });
    }

    // 🎯 로그인 검증 (이름 + 전화번호)
    public User loginUser(String name, String phone) {
        try {
            String normalizedPhone = User.normalizePhoneNumber(phone);
            Future<User> future = executor.submit(() ->
                    userDao.getUserByNameAndPhoneSync(name, normalizedPhone));
            User user = future.get();

            if (user != null) {
                // 로그인 시간 업데이트
                updateLastLogin(user.getUserId());
                Log.d(TAG, "✅ 로그인 성공: " + name);
            } else {
                Log.d(TAG, "❌ 로그인 실패: 사용자를 찾을 수 없음");
            }
            return user;
        } catch (Exception e) {
            Log.e(TAG, "❌ 로그인 처리 중 오류", e);
            return null;
        }
    }

    // 🎯 사용자 존재 여부 확인
    public boolean isUserExists(String name, String phone) {
        try {
            String normalizedPhone = User.normalizePhoneNumber(phone);
            Future<Integer> future = executor.submit(() ->
                    userDao.getUserCount(name, normalizedPhone));
            return future.get() > 0;
        } catch (Exception e) {
            Log.e(TAG, "❌ 사용자 존재 확인 실패", e);
            return false;
        }
    }

    // 🎯 사용자 정보 조회 (LiveData)
    public LiveData<User> getUserByNameAndPhone(String name, String phone) {
        String normalizedPhone = User.normalizePhoneNumber(phone);
        return userDao.getUserByNameAndPhone(name, normalizedPhone);
    }

    // 🎯 사용자 정보 조회 (동기 방식)
    public User getUserByNameAndPhoneSync(String name, String phone) {
        try {
            String normalizedPhone = User.normalizePhoneNumber(phone);
            Future<User> future = executor.submit(() ->
                    userDao.getUserByNameAndPhoneSync(name, normalizedPhone));
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "❌ 사용자 정보 조회 실패", e);
            return null;
        }
    }

    // 🎯 UUID로 사용자 조회
    public User getUserById(String userId) {
        try {
            Future<User> future = executor.submit(() -> userDao.getUserByIdSync(userId));
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "❌ UUID로 사용자 조회 실패", e);
            return null;
        }
    }

    // 🎯 게임 진행 상황 업데이트
    public void updateGameProgress(String userId, int currentStory, int completedStories) {
        executor.execute(() -> {
            try {
                userDao.updateGameProgress(userId, currentStory, completedStories);
                Log.d(TAG, "✅ 게임 진행 상황 업데이트: Story " + currentStory + ", 완료: " + completedStories);
            } catch (Exception e) {
                Log.e(TAG, "❌ 게임 진행 상황 업데이트 실패", e);
            }
        });
    }

    // 🎯 로그인 시간 업데이트
    private void updateLastLogin(String userId) {
        executor.execute(() -> {
            try {
                userDao.updateLastLogin(userId, System.currentTimeMillis());
                Log.d(TAG, "✅ 로그인 시간 업데이트 완료");
            } catch (Exception e) {
                Log.e(TAG, "❌ 로그인 시간 업데이트 실패", e);
            }
        });
    }

    // 🎯 모든 사용자 조회
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    // 🎯 리소스 정리
    public void shutdown() {
        executor.shutdown();
    }
}