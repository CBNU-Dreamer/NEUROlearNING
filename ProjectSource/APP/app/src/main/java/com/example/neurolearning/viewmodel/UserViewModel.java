package com.example.neurolearning.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserDatabase;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final UserDatabase db;

    public UserViewModel(@NonNull Application application) {
        super(application);
        db = UserDatabase.getInstance(application);
    }

    public void insertUser(User user) {
        new Thread(() -> db.userDao().insertUser(user)).start();  // Room은 메인스레드 사용 금지!
    }

    public LiveData<List<User>> getAllUsers() {
        return db.userDao().getAllUsers();  // LiveData를 사용할 경우 Room에서 자동으로 처리
    }

    public LiveData<User> getUserByUsername(String username) {
        return db.userDao().getUserByUsername(username);
    }
}