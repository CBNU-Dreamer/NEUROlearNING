package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class Story7Activity extends AppCompatActivity {
    private static final int REQ_GAME = 100; // Story3에 맞는 게임 요청 코드
    private FrameLayout contentFrame;

    // DB 관련 추가
    private GameProgressRepository gameProgressRepository;
    private String currentUsername;
    private int currentStoryNumber = 7; // Story3Activity이므로 3번 스토리
    private long gameStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story7); // Story3 레이아웃

        // 사용자 정보 가져오기 (추가)
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "testuser"; // 임시값
        }

        // Repository 초기화 (추가)
        gameProgressRepository = new GameProgressRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);
        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story7, contentFrame, false); // Story3 시작 레이아웃
        Button btn = initial.findViewById(R.id.btnStartGame);

        btn.setOnClickListener(v -> {
            gameStartTime = System.currentTimeMillis(); // 게임 시작 시간 기록 (추가)

            // Story3에 맞는 게임 Activity 호출 (예시: 퍼즐 게임, 다른 미니게임 등)
            Intent intent = new Intent(Story7Activity.this, /* Story3Game */NameGameActivity.class);
            // 사용자 정보 전달 (추가)
            intent.putExtra("username", currentUsername);
            intent.putExtra("storyNumber", currentStoryNumber);
            startActivityForResult(intent, REQ_GAME);
        });

        contentFrame.addView(initial);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GAME && resultCode == RESULT_OK) {
            // 게임 성공 시 DB 업데이트 (추가 부분)
            long completionTime = System.currentTimeMillis() - gameStartTime;
            int score = data != null ? data.getIntExtra("score", 100) : 100; // 기본 점수

            // 1. 게임 플레이 기록 저장
            gameProgressRepository.saveGamePlayRecord(
                    currentUsername,
                    currentStoryNumber,
                    "STORY7_GAME", // Story3에 맞는 게임 타입으로 변경
                    score,
                    true,
                    0,
                    completionTime / 1000 // 초 단위로 변환
            );

            // 2. 스토리 완료 처리 (다음 스토리 해제)
            gameProgressRepository.completeStory(currentUsername, currentStoryNumber);

            showEndScreen();
        } else if (requestCode == REQ_GAME) {
            Toast.makeText(this, "게임이 정상 종료되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEndScreen() {
        contentFrame.removeAllViews();
        View end = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story7, contentFrame, false); // Story3 종료 레이아웃
        Button btn = end.findViewById(R.id.btnEnd);

        btn.setOnClickListener(v -> finish());
        contentFrame.addView(end);
    }
}