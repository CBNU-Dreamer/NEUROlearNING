package com.example.neurolearning.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameProgressDao {

    // UserGameStatus 관련 (사용자 전체 진행현황)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserGameStatus(UserGameStatus userGameStatus);

    @Update
    void updateUserGameStatus(UserGameStatus userGameStatus);

    @Query("SELECT * FROM user_game_status WHERE username = :username")
    LiveData<UserGameStatus> getUserGameStatus(String username);

    @Query("UPDATE user_game_status SET current_story = :currentStory, total_completed_stories = :completedCount WHERE username = :username")
    void updateUserProgress(String username, int currentStory, int completedCount);

    // StoryStatus 관련 (각 스토리별 진행상황)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoryStatus(StoryStatus storyStatus);

    @Update
    void updateStoryStatus(StoryStatus storyStatus);

    @Query("SELECT * FROM story_status WHERE username = :username ORDER BY story_number")
    LiveData<List<StoryStatus>> getUserStoryStatuses(String username);

    @Query("SELECT * FROM story_status WHERE username = :username AND story_number = :storyNumber")
    LiveData<StoryStatus> getStoryStatus(String username, int storyNumber);

    @Query("SELECT COUNT(*) FROM story_status WHERE username = :username AND is_completed = 1")
    LiveData<Integer> getCompletedStoryCount(String username);

    @Query("UPDATE story_status SET is_completed = 1, completion_date = :completionDate WHERE username = :username AND story_number = :storyNumber")
    void completeStory(String username, int storyNumber, long completionDate);

    @Query("UPDATE story_status SET play_count = play_count + 1 WHERE username = :username AND story_number = :storyNumber")
    void incrementStoryPlayCount(String username, int storyNumber);

    // GamePlayRecord 관련 (실제 게임 플레이 기록)
    @Insert
    void insertGamePlayRecord(GamePlayRecord record);

    @Query("SELECT * FROM game_play_record WHERE username = :username AND story_number = :storyNumber ORDER BY play_date DESC")
    LiveData<List<GamePlayRecord>> getGamePlayRecords(String username, int storyNumber);

    @Query("SELECT * FROM game_play_record WHERE username = :username ORDER BY play_date DESC LIMIT 10")
    LiveData<List<GamePlayRecord>> getRecentGamePlayRecords(String username);

    @Query("SELECT * FROM game_play_record WHERE username = :username AND game_type = :gameType ORDER BY score DESC LIMIT 1")
    LiveData<GamePlayRecord> getBestGameRecord(String username, String gameType);
}