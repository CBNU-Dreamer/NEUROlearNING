package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_game_status",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "username",
                childColumns = "username",
                onDelete = ForeignKey.CASCADE))
public class UserGameStatus {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    public String username;  // 사용자 ID (User 테이블과 연결)

    @ColumnInfo(name = "current_story")
    public int currentStory;  // 현재 진행 중인 스토리 번호 (1~9)

    @ColumnInfo(name = "total_completed_stories")
    public int totalCompletedStories;  // 완료한 총 스토리 수

    @ColumnInfo(name = "last_played_date")
    public long lastPlayedDate;  // 마지막 플레이 날짜 (timestamp)

    @ColumnInfo(name = "total_play_time")
    public long totalPlayTime;  // 총 플레이 시간 (분 단위)

    public UserGameStatus(@NonNull String username, int currentStory, int totalCompletedStories,
                          long lastPlayedDate, long totalPlayTime) {
        this.username = username;
        this.currentStory = currentStory;
        this.totalCompletedStories = totalCompletedStories;
        this.lastPlayedDate = lastPlayedDate;
        this.totalPlayTime = totalPlayTime;
    }
}