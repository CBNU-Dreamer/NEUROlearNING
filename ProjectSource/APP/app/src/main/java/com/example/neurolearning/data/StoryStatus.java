package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "story_status",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "username",
                childColumns = "username",
                onDelete = ForeignKey.CASCADE),
        primaryKeys = {"username", "story_number"})
public class StoryStatus {

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @NonNull
    @ColumnInfo(name = "story_number")
    public int storyNumber;  // 스토리 번호 (1~9)

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;  // 스토리 완료 여부

    @ColumnInfo(name = "completion_date")
    public long completionDate;  // 완료 날짜

    @ColumnInfo(name = "play_count")
    public int playCount;  // 플레이 횟수

    @ColumnInfo(name = "best_score")
    public int bestScore;  // 최고 점수 (해당하는 경우)

    @ColumnInfo(name = "story_play_time")
    public long storyPlayTime;  // 이 스토리 총 플레이 시간 (초 단위)

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