package com.example.neurolearning.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    // 🎯 사용자 정보 업데이트
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    // 🎯 UUID로 사용자 조회
    public User getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    // 🎯 이름과 전화번호로 사용자 조회 (LiveData)
    public LiveData<User> getUserByNameAndPhone(String name, String phone) {
        return userRepository.getUserByNameAndPhone(name, phone);
    }

    // 🎯 이름과 전화번호로 사용자 조회 (동기 방식)
    public User getUserByNameAndPhoneSync(String name, String phone) {
        return userRepository.getUserByNameAndPhoneSync(name, phone);
    }

    // 🎯 로그인 검증
    public User loginUser(String name, String phone) {
        return userRepository.loginUser(name, phone);
    }

    // 🎯 모든 사용자 조회
    public LiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    // 🎯 사용자 존재 여부 확인
    public boolean isUserExists(String name, String phone) {
        return userRepository.isUserExists(name, phone);
    }

    // 🎯 게임 진행 상황 업데이트
    public void updateGameProgress(String userId, int currentStory, int completedStories) {
        userRepository.updateGameProgress(userId, currentStory, completedStories);
    }

    // 🎯 리소스 정리
    @Override
    protected void onCleared() {
        super.onCleared();
        userRepository.shutdown();
    }
}