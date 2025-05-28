package com.example.neurolearning.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GameProgressRepository {
    private final GameProgressDao gameProgressDao;
    private final UserDatabase db;

    public GameProgressRepository(Application application) {
        db = UserDatabase.getInstance(application);
        gameProgressDao = db.gameProgressDao();
    }

    // 사용자 게임 현황 초기화 (처음 가입 시)
    public void initializeUserGameStatus(String username) {
        new Thread(() -> {
            // 1. 전체 게임 현황 초기화
            UserGameStatus userStatus = new UserGameStatus(username, 1, 0,
                    System.currentTimeMillis(), 0);
            gameProgressDao.insertUserGameStatus(userStatus);

            // 2. 모든 스토리 상태 초기화 (1~9번 스토리)
            for (int i = 1; i <= 9; i++) {
                StoryStatus storyStatus = new StoryStatus(username, i, false, 0, 0, 0, 0);
                gameProgressDao.insertStoryStatus(storyStatus);
            }
        }).start();
    }

    // 스토리 완료 처리
    public void completeStory(String username, int storyNumber) {
        new Thread(() -> {
            long currentTime = System.currentTimeMillis();

            // 1. 해당 스토리를 완료 상태로 변경
            gameProgressDao.completeStory(username, storyNumber, currentTime);

            // 2. 전체 게임 현황 업데이트 (다음 스토리 해제)
            int nextStory = Math.min(storyNumber + 1, 9);
            gameProgressDao.updateUserProgress(username, nextStory, storyNumber);
        }).start();
    }

    // 게임 플레이 기록 저장
    public void saveGamePlayRecord(String username, int storyNumber, String gameType,
                                   int score, boolean isSuccess, int mistakes, long completionTime) {
        new Thread(() -> {
            // 1. 게임 플레이 기록 저장
            GamePlayRecord record = new GamePlayRecord(username, storyNumber, gameType,
                    score, isSuccess, System.currentTimeMillis(),
                    mistakes, completionTime);
            gameProgressDao.insertGamePlayRecord(record);

            // 2. 스토리 플레이 카운트 증가
            gameProgressDao.incrementStoryPlayCount(username, storyNumber);
        }).start();
    }

    // LiveData 반환 메서드들
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