package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_play_record")
// 🎯 Foreign Key 완전 제거 - 참조 무결성보다 안정성 우선
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
    public String gameType;

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "is_success")
    public boolean isSuccess;

    @ColumnInfo(name = "play_date")
    public long playDate;

    @ColumnInfo(name = "mistakes_count")
    public int mistakesCount;

    @ColumnInfo(name = "completion_time")
    public long completionTime;

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