package com.example.neurolearning.viewmodel;

import androidx.lifecycle.LiveData;
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

    public void insertUser(User user) {
        userRepository.insertUser(user);
    }

    public LiveData<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
}
