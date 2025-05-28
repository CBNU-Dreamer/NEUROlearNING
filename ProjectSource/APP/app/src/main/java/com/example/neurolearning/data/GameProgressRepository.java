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

    // 🎯 안전한 초기화 - 동기화 개선
    public void initializeUserGameStatus(String username) {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== 사용자 게임 상태 초기화 시작: " + username + " ===");

                // 1. UserGameStatus 먼저 초기화
                UserGameStatus userStatus = new UserGameStatus(username, 1, 0,
                        System.currentTimeMillis(), 0);
                gameProgressDao.insertUserGameStatus(userStatus);
                Log.d(TAG, "✅ UserGameStatus 삽입 완료: " + username);

                // 2. StoryStatus 초기화 (1~9번 스토리) - 각각 로그 출력
                for (int i = 1; i <= 9; i++) {
                    StoryStatus storyStatus = new StoryStatus(username, i, false, 0, 0, 0, 0);
                    gameProgressDao.insertStoryStatus(storyStatus);
                    Log.d(TAG, "📖 StoryStatus 삽입: Story" + i + " for " + username);
                }
                Log.d(TAG, "✅ 모든 StoryStatus 삽입 완료: " + username);
                Log.d(TAG, "=== 초기화 완전 완료: " + username + " ===");

            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 게임 상태 초기화 실패: " + username, e);
            }
        }).start();
    }

    // 🎯 수정된 스토리 완료 처리 - 매개변수 순서 수정
    public void completeStory(String username, int storyNumber) {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== 스토리 완료 처리 시작: " + username + ", Story" + storyNumber + " ===");

                long currentTime = System.currentTimeMillis();

                // 1. 해당 스토리를 완료 상태로 변경
                gameProgressDao.completeStory(username, storyNumber, currentTime);
                Log.d(TAG, "📖 StoryStatus 완료 처리: Story" + storyNumber);

                // 2. 🎯 수정: 완료된 스토리 수 계산 및 업데이트
                int completedCount = storyNumber; // Story1 완료하면 1개 완료
                int nextStory = Math.min(storyNumber + 1, 9); // 다음 스토리는 2

                Log.d(TAG, "📊 업데이트 정보: completedCount=" + completedCount + ", nextStory=" + nextStory);

                // 🎯 매개변수 순서 수정: currentStory, completedCount 순서
                gameProgressDao.updateUserProgress(username, nextStory, completedCount);
                Log.d(TAG, "✅ UserGameStatus 업데이트 완료: nextStory=" + nextStory + ", completed=" + completedCount);

                Log.d(TAG, "=== 스토리 완료 처리 성공: " + username + " ===");
            } catch (Exception e) {
                Log.e(TAG, "❌ 스토리 완료 처리 실패: " + username, e);
            }
        }).start();
    }

    // 기존 saveGamePlayRecord는 그대로 유지...
    public void saveGamePlayRecord(String username, int storyNumber, String gameType,
                                   int score, boolean isSuccess, int mistakes, long completionTime) {
        new Thread(() -> {
            try {
                Log.d(TAG, "게임 기록 저장 시작: " + username + ", " + gameType + ", 점수: " + score);

                GamePlayRecord record = new GamePlayRecord(username, storyNumber, gameType,
                        score, isSuccess, System.currentTimeMillis(),
                        mistakes, completionTime);
                gameProgressDao.insertGamePlayRecord(record);
                Log.d(TAG, "GamePlayRecord 삽입 완료");

                try {
                    gameProgressDao.incrementStoryPlayCount(username, storyNumber);
                    Log.d(TAG, "StoryStatus 플레이 카운트 업데이트 완료");
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