package com.example.neurolearning.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GameRecordDao {

    // 🎯 게임 기록 저장
    @Insert
    void insertGameRecord(GamePlayRecord record);

    // 🎯 특정 사용자의 모든 게임 기록
    @Query("SELECT * FROM game_play_record WHERE user_id = :userId ORDER BY play_date DESC")
    LiveData<List<GamePlayRecord>> getUserGameRecords(String userId);

    // 🎯 특정 사용자의 특정 스토리 기록
    @Query("SELECT * FROM game_play_record WHERE user_id = :userId AND story_number = :storyNumber ORDER BY play_date DESC")
    LiveData<List<GamePlayRecord>> getStoryGameRecords(String userId, int storyNumber);

    // 🎯 특정 사용자의 최근 게임 기록 (최대 10개)
    @Query("SELECT * FROM game_play_record WHERE user_id = :userId ORDER BY play_date DESC LIMIT 10")
    LiveData<List<GamePlayRecord>> getRecentGameRecords(String userId);

    // 🎯 특정 게임 타입의 최고 점수
    @Query("SELECT * FROM game_play_record WHERE user_id = :userId AND game_type = :gameType ORDER BY score DESC LIMIT 1")
    LiveData<GamePlayRecord> getBestGameRecord(String userId, String gameType);

    // 🎯 특정 스토리의 성공한 기록만 조회
    @Query("SELECT * FROM game_play_record WHERE user_id = :userId AND story_number = :storyNumber AND is_success = 1 ORDER BY play_date DESC")
    LiveData<List<GamePlayRecord>> getSuccessGameRecords(String userId, int storyNumber);

    // 🎯 전체 플레이 횟수
    @Query("SELECT COUNT(*) FROM game_play_record WHERE user_id = :userId")
    LiveData<Integer> getTotalPlayCount(String userId);

    // 🎯 특정 스토리 플레이 횟수
    @Query("SELECT COUNT(*) FROM game_play_record WHERE user_id = :userId AND story_number = :storyNumber")
    LiveData<Integer> getStoryPlayCount(String userId, int storyNumber);
}