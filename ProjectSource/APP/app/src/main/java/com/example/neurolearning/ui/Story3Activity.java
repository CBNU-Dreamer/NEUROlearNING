package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.GameRecordRepository;

public class Story3Activity extends AppCompatActivity {
    private static final String TAG = "Story3Activity";
    private static final int REQ_GAME = 100;

    private FrameLayout contentFrame;
    private GameRecordRepository gameRecordRepository;

    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber = 3;
    private long gameStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story3);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Story3Activity 시작: " + currentUserName);

        // Repository 초기화
        gameRecordRepository = new GameRecordRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);
        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story3, contentFrame, false); // 레이아웃 재사용
        Button btn = initial.findViewById(R.id.btnStartGame);

        btn.setOnClickListener(v -> {
            gameStartTime = System.currentTimeMillis();

            // Story3에 맞는 게임 (가위바위보) 시작
            Intent intent = new Intent(Story3Activity.this, RspGameActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("userName", currentUserName);
            intent.putExtra("storyNumber", currentStoryNumber);
            startActivityForResult(intent, REQ_GAME);
        });

        contentFrame.addView(initial);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_GAME && resultCode == RESULT_OK) {
            // 게임 성공 시 DB 업데이트
            long completionTime = System.currentTimeMillis() - gameStartTime;
            int score = data != null ? data.getIntExtra("score", 100) : 100;

            Log.d(TAG, "✅ Story3 가위바위보 게임 완료 - 점수: " + score + ", 소요시간: " + (completionTime/1000) + "초");

            // 🎯 새로운 DB 구조에 맞는 게임 기록 저장
            gameRecordRepository.saveGameRecord(
                    currentUserId,
                    currentUserName,
                    currentStoryNumber,
                    "ROCK_PAPER_SCISSORS",
                    score,
                    true, // 성공
                    0, // 가위바위보는 실수 횟수 측정 안함
                    completionTime / 1000 // 초 단위
            );

            showEndScreen();
        } else if (requestCode == REQ_GAME) {
            Toast.makeText(this, "게임이 정상 종료되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEndScreen() {
        contentFrame.removeAllViews();
        View end = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story3, contentFrame, false); // 레이아웃 재사용
        Button btn = end.findViewById(R.id.btnEnd);

        btn.setOnClickListener(v -> {
            Log.d(TAG, "Story3 완료 - StoryActivity로 복귀");
            finish();
        });

        contentFrame.addView(end);
    }
}