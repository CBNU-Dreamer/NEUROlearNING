package com.example.neurolearning.data;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;

public class GameProgressRepository {
    private static final String TAG = "GameProgressRepo";
    private final GameProgressDao gameProgressDao;
    private final UserDatabase db;

    public GameProgressRepository(Application application) {
        try {
            db = UserDatabase.getInstance(application);
            gameProgressDao = db.gameProgressDao();
            Log.d(TAG, "GameProgressRepository 초기화 성공");
        } catch (Exception e) {
            Log.e(TAG, "GameProgressRepository 초기화 실패", e);
            throw new RuntimeException("Database 초기화 실패", e);
        }
    }

    // 🎯 안전한 초기화 (트랜잭션 순서 개선)
    public void initializeUserGameStatus(String username) {
        new Thread(() -> {
            try {
                Log.d(TAG, "사용자 게임 상태 초기화 시작: " + username);

                // 1. UserGameStatus 먼저 초기화
                UserGameStatus userStatus = new UserGameStatus(username, 1, 0,
                        System.currentTimeMillis(), 0);
                gameProgressDao.insertUserGameStatus(userStatus);
                Log.d(TAG, "UserGameStatus 삽입 완료");

                // 2. StoryStatus 초기화 (1~9번 스토리)
                for (int i = 1; i <= 9; i++) {
                    StoryStatus storyStatus = new StoryStatus(username, i, false, 0, 0, 0, 0);
                    gameProgressDao.insertStoryStatus(storyStatus);
                }
                Log.d(TAG, "모든 StoryStatus 삽입 완료");

            } catch (Exception e) {
                Log.e(TAG, "사용자 게임 상태 초기화 실패: " + username, e);
            }
        }).start();
    }

    // 🎯 안전한 스토리 완료 처리
    public void completeStory(String username, int storyNumber) {
        new Thread(() -> {
            try {
                Log.d(TAG, "스토리 완료 처리: " + username + ", Story" + storyNumber);

                long currentTime = System.currentTimeMillis();

                // 1. 해당 스토리를 완료 상태로 변경
                gameProgressDao.completeStory(username, storyNumber, currentTime);

                // 2. 전체 게임 현황 업데이트
                int nextStory = Math.min(storyNumber + 1, 9);
                gameProgressDao.updateUserProgress(username, nextStory, storyNumber);

                Log.d(TAG, "스토리 완료 처리 성공");
            } catch (Exception e) {
                Log.e(TAG, "스토리 완료 처리 실패", e);
            }
        }).start();
    }

    // 🎯 가장 안전한 게임 기록 저장
    public void saveGamePlayRecord(String username, int storyNumber, String gameType,
                                   int score, boolean isSuccess, int mistakes, long completionTime) {
        new Thread(() -> {
            try {
                Log.d(TAG, "게임 기록 저장 시작: " + username + ", " + gameType + ", 점수: " + score);

                // 1. 게임 플레이 기록 저장
                GamePlayRecord record = new GamePlayRecord(username, storyNumber, gameType,
                        score, isSuccess, System.currentTimeMillis(),
                        mistakes, completionTime);
                gameProgressDao.insertGamePlayRecord(record);
                Log.d(TAG, "GamePlayRecord 삽입 완료");

                // 2. 스토리 플레이 카운트 증가 (안전하게)
                try {
                    gameProgressDao.incrementStoryPlayCount(username, storyNumber);
                    Log.d(TAG, "StoryStatus 업데이트 완료");
                } catch (Exception e) {
                    Log.w(TAG, "StoryStatus 업데이트 실패하지만 기록 저장은 성공", e);
                }

                Log.d(TAG, "게임 기록 저장 완전 성공");
            } catch (Exception e) {
                Log.e(TAG, "게임 기록 저장 실패", e);
            }
        }).start();
    }

    // LiveData 반환 메서드들 (기존과 동일)
    public LiveData<UserGameStatus> getUserGameStatus(String username) {
        return gameProgressDao.getUserGameStatus(username);
    }

    public LiveData<List<StoryStatus>> getUserStoryStatuses(String username) {
        return gameProgressDao.getUserStoryStatuses(username);
    }

    public LiveData<Integer> getCompletedStoryCount(String username) {
        return gameProgressDao.getCompletedStoryCount(username);
    }

    public LiveData<List<GamePlayRecord>> getRecentGamePlayRecords(String username) {
        return gameProgressDao.getRecentGamePlayRecords(username);
    }

    public LiveData<GamePlayRecord> getBestGameRecord(String username, String gameType) {
        return gameProgressDao.getBestGameRecord(username, gameType);
    }
}