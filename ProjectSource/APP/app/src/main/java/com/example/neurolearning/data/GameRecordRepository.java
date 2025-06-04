package com.example.neurolearning.data;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRecordRepository {
    private static final String TAG = "GameRecordRepo";
    private final GameRecordDao gameRecordDao;
    private final UserRepository userRepository;
    private final ExecutorService executor;

    public GameRecordRepository(Application application) {
        UserDatabase db = UserDatabase.getInstance(application);
        gameRecordDao = db.gameRecordDao();
        userRepository = new UserRepository(application);
        executor = Executors.newFixedThreadPool(2);
    }

    // 🎯 게임 기록 저장 및 사용자 진행 상황 업데이트
    public void saveGameRecord(String userId, String userName, int storyNumber,
                               String gameType, int score, boolean isSuccess,
                               int mistakes, long completionTime) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "=== 게임 기록 저장 시작 ===");
                Log.d(TAG, "사용자: " + userName + ", 스토리: " + storyNumber + ", 성공: " + isSuccess);

                // 1. 게임 기록 저장
                GamePlayRecord record = new GamePlayRecord(
                        userId, userName, storyNumber, gameType, score,
                        isSuccess, System.currentTimeMillis(), mistakes, completionTime
                );
                gameRecordDao.insertGameRecord(record);
                Log.d(TAG, "✅ 게임 기록 저장 완료");

                // 2. 스토리 완료 시 사용자 진행 상황 업데이트
                if (isSuccess) {
                    // 현재 사용자 정보 조회
                    User currentUser = userRepository.getUserById(userId);
                    if (currentUser != null) {
                        int currentStory = currentUser.getCurrentStory();
                        int completedStories = currentUser.getTotalCompletedStories();

                        // 현재 진행 중인 스토리를 완료한 경우
                        if (storyNumber == currentStory) {
                            int newCompletedStories = completedStories + 1;
                            int newCurrentStory = Math.min(storyNumber + 1, 9); // 최대 9번 스토리

                            userRepository.updateGameProgress(userId, newCurrentStory, newCompletedStories);
                            Log.d(TAG, "✅ 진행 상황 업데이트: 현재 스토리 " + newCurrentStory + ", 완료 " + newCompletedStories);
                        } else {
                            Log.d(TAG, "ℹ️ 이미 완료된 스토리 재플레이 - 진행 상황 업데이트 안함");
                        }
                    }
                }

                Log.d(TAG, "=== 게임 기록 저장 완료 ===");
            } catch (Exception e) {
                Log.e(TAG, "❌ 게임 기록 저장 실패", e);
            }
        });
    }

    // 🎯 특정 사용자의 모든 게임 기록
    public LiveData<List<GamePlayRecord>> getUserGameRecords(String userId) {
        return gameRecordDao.getUserGameRecords(userId);
    }

    // 🎯 특정 스토리의 게임 기록
    public LiveData<List<GamePlayRecord>> getStoryGameRecords(String userId, int storyNumber) {
        return gameRecordDao.getStoryGameRecords(userId, storyNumber);
    }

    // 🎯 최근 게임 기록
    public LiveData<List<GamePlayRecord>> getRecentGameRecords(String userId) {
        return gameRecordDao.getRecentGameRecords(userId);
    }

    // 🎯 최고 점수 기록
    public LiveData<GamePlayRecord> getBestGameRecord(String userId, String gameType) {
        return gameRecordDao.getBestGameRecord(userId, gameType);
    }

    // 🎯 전체 플레이 횟수
    public LiveData<Integer> getTotalPlayCount(String userId) {
        return gameRecordDao.getTotalPlayCount(userId);
    }

    // 🎯 특정 스토리 플레이 횟수
    public LiveData<Integer> getStoryPlayCount(String userId, int storyNumber) {
        return gameRecordDao.getStoryPlayCount(userId, storyNumber);
    }

    // 🎯 리소스 정리
    public void shutdown() {
        executor.shutdown();
    }
}