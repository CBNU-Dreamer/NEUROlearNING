package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_play_record",
        foreignKeys = @ForeignKey(entity = StoryStatus.class,
                parentColumns = {"username", "story_number"},
                childColumns = {"username", "story_number"},
                onDelete = ForeignKey.CASCADE))
public class GamePlayRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "story_number")
    public int storyNumber;

    @ColumnInfo(name = "game_type")
    public String gameType;  // "KIOSK", "MEMORY", "PUZZLE" 등

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "is_success")
    public boolean isSuccess;  // 성공/실패 여부

    @ColumnInfo(name = "play_date")
    public long playDate;

    @ColumnInfo(name = "mistakes_count")
    public int mistakesCount;  // 실수 횟수

    @ColumnInfo(name = "completion_time")
    public long completionTime;  // 완료 시간 (초)

    public GamePlayRecord(@NonNull String username, int storyNumber, String gameType,
                          int score, boolean isSuccess, long playDate,
                          int mistakesCount, long completionTime) {
        this.username = username;
        this.storyNumber = storyNumber;
        this.gameType = gameType;
        this.score = score;
        this.isSuccess = isSuccess;
        this.playDate = playDate;
        this.mistakesCount = mistakesCount;
        this.completionTime = completionTime;
    }
}