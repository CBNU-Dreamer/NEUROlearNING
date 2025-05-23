package com.example.neurolearning.data;

import com.example.neurolearning.data.User;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();
}
