package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_game_status")
// 🎯 Foreign Key 제거 - 독립적인 테이블로 관리
public class UserGameStatus {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "current_story")
    public int currentStory;

    @ColumnInfo(name = "total_completed_stories")
    public int totalCompletedStories;

    @ColumnInfo(name = "last_played_date")
    public long lastPlayedDate;

    @ColumnInfo(name = "total_play_time")
    public long totalPlayTime;

    public UserGameStatus(@NonNull String username, int currentStory, int totalCompletedStories,
                          long lastPlayedDate, long totalPlayTime) {
        this.username = username;
        this.currentStory = currentStory;
        this.totalCompletedStories = totalCompletedStories;
        this.lastPlayedDate = lastPlayedDate;
        this.totalPlayTime = totalPlayTime;
    }
}