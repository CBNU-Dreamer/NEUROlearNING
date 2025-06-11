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

public class Story9Activity extends AppCompatActivity {
    private static final int REQ_SHOPPING_GAME = 206; // 스토리6용 요청 코드

    private FrameLayout contentFrame;
    private GameProgressRepository gameProgressRepository;
    private String currentUsername;
    private final int currentStoryNumber = 9; // Story6Activity 이므로 6
    private long gameStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story9);
        // → activity_story6.xml 에 FrameLayout(contentFrame) 하나만 정의

        // 인텐트로부터 username 받기 (없으면 기본값 사용)
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "testuser";
        }

        // Repository 초기화 (DB 저장용)
        gameProgressRepository = new GameProgressRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);
        showStartScreen();
    }

    /**
     * [1] 스토리6 “시작 화면” (activity_start_story6.xml) 표시
     */
    private void showStartScreen() {
        contentFrame.removeAllViews();
        View startView = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story9, contentFrame, false);

        Button btnStartGame = startView.findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(v -> {
            // 게임 시작 시간 기록
            gameStartTime = System.currentTimeMillis();

            // ShoppingCartGameActivity 호출 (스토리 번호는 6)
            Intent intent = new Intent(Story9Activity.this, LetterGameActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("storyNumber", currentStoryNumber);
            startActivityForResult(intent, REQ_SHOPPING_GAME);
        });
        contentFrame.addView(startView);
    }

    /**
     * [2] ShoppingCartGameActivity 완료 후 결과 처리
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SHOPPING_GAME) {
            if (resultCode == RESULT_OK && data != null) {
                // 게임에서 반환된 점수와 시간
                int score = data.getIntExtra("score", 0);
                long completionTime = data.getLongExtra("completionTime", 0);

                // [A] 게임 기록은 ShoppingCartGameActivity에서 이미 저장했으므로,
                //     추가 로직이 필요하다면 여기서 수행 가능

                // [B] 스토리6 완료 처리 (다음 스토리 잠금 해제 등)
                gameProgressRepository.completeStory(currentUsername, currentStoryNumber);

                // [C] “종료 화면” 표시
                showEndScreen();
            } else {
                // 게임이 비정상 종료된 경우
                Toast.makeText(this, "게임이 정상적으로 종료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                showStartScreen(); // 원한다면 다시 시작 화면으로 돌아감
            }
        }
    }

    /**
     * [3] 스토리6 “종료 화면” (activity_end_story6.xml) 표시
     */
    private void showEndScreen() {
        contentFrame.removeAllViews();
        View endView = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story9, contentFrame, false);

        Button btnEnd = endView.findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(v -> {
            // Story6Activity 종료 (이후 부모 Activity가 있으면 해당 화면으로 돌아갑니다)
            finish();
        });
        contentFrame.addView(endView);
    }
}
