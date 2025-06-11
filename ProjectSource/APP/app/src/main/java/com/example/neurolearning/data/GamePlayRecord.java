package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//====게임 한판당의 기록======
//자체PK, UUID, 이름,

@Entity(tableName = "game_play_record")
public class GamePlayRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "user_id") // User의 UUID 참조
    public String userId;

    @NonNull
    @ColumnInfo(name = "user_name") // 검색 편의를 위한 이름 저장
    public String userName;

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

    public GamePlayRecord(@NonNull String userId, @NonNull String userName,
                          int storyNumber, String gameType, int score,
                          boolean isSuccess, long playDate, int mistakesCount,
                          long completionTime) {
        this.userId = userId;
        this.userName = userName;
        this.storyNumber = storyNumber;
        this.gameType = gameType;
        this.score = score;
        this.isSuccess = isSuccess;
        this.playDate = playDate;
        this.mistakesCount = mistakesCount;
        this.completionTime = completionTime;
    }
}