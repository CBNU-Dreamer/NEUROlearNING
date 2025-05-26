package com.example.neurolearning.data;

import com.example.neurolearning.data.User;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    // 기존 LiveData 방식 (비동기용)
    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();

    // UserInfoActivity에서 사용할 동기 방식 메서드들 추가
    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    User getUserByUsernameSync(String username);

    // 사용자 존재 여부 확인
    @Query("SELECT COUNT(*) FROM user_table WHERE username = :username")
    int getUserCount(String username);

    // 로그인 검증용 (아이디와 비밀번호 확인)
    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password LIMIT 1")
    User loginUser(String username, String password);
}