package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "story_status",
        primaryKeys = {"username", "story_number"})
// 🎯 Foreign Key 제거 - User 테이블과의 강한 결합 해제
public class StoryStatus {

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @NonNull
    @ColumnInfo(name = "story_number")
    public int storyNumber;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "completion_date")
    public long completionDate;

    @ColumnInfo(name = "play_count")
    public int playCount;

    @ColumnInfo(name = "best_score")
    public int bestScore;

    @ColumnInfo(name = "story_play_time")
    public long storyPlayTime;

    public StoryStatus(@NonNull String username, int storyNumber,
                       boolean isCompleted, long completionDate,
                       int playCount, int bestScore, long storyPlayTime) {
        this.username = username;
        this.storyNumber = storyNumber;
        this.isCompleted = isCompleted;
        this.completionDate = completionDate;
        this.playCount = playCount;
        this.bestScore = bestScore;
        this.storyPlayTime = storyPlayTime;
    }
}
