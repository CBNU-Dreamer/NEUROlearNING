package com.example.neurolearning.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;

public class SignUpViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    // 🎯 새로운 User.java에 맞는 사용자 등록
    public void insertUser(User user) {
        userRepository.insertUser(user);
    }

    // 🎯 사용자 존재 여부 확인 (이름 + 전화번호)
    public boolean isUserExists(String name, String phone) {
        return userRepository.isUserExists(name, phone);
    }
}