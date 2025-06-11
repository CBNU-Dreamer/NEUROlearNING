package com.example.neurolearning.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    // 새로운 User.java의 로그인 방식에 맞춤 (이름 + 전화번호)
    @Query("SELECT * FROM user_table WHERE name = :name AND phone = :phone LIMIT 1")
    LiveData<User> getUserByNameAndPhone(String name, String phone);

    // 동기 방식 (UserInfoActivity, 로그인 등에서 사용)
    @Query("SELECT * FROM user_table WHERE name = :name AND phone = :phone LIMIT 1")
    User getUserByNameAndPhoneSync(String name, String phone);

    // 검색 키로 사용자 찾기 (내부적으로 사용)
    @Query("SELECT * FROM user_table WHERE search_key = :searchKey LIMIT 1")
    User getUserBySearchKey(String searchKey);

    // 모든 사용자 조회
    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();

    // 사용자 존재 여부 확인 (이름 + 전화번호)
    @Query("SELECT COUNT(*) FROM user_table WHERE name = :name AND phone = :phone")
    int getUserCount(String name, String phone);

    // UUID로 사용자 조회 (내부적으로 사용)
    @Query("SELECT * FROM user_table WHERE user_id = :userId LIMIT 1")
    User getUserByIdSync(String userId);

    // 게임 진행 상황 업데이트 (User 테이블에서 직접 관리)
    @Query("UPDATE user_table SET current_story = :currentStory, total_completed_stories = :completedStories WHERE user_id = :userId")
    void updateGameProgress(String userId, int currentStory, int completedStories);

    // 로그인 시간 업데이트
    @Query("UPDATE user_table SET last_login = :loginTime WHERE user_id = :userId")
    void updateLastLogin(String userId, long loginTime);
}