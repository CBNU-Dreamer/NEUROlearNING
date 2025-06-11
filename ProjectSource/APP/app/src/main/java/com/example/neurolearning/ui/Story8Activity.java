package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.GameRecordRepository;

public class Story8Activity extends AppCompatActivity {
    private static final String TAG = "Story8Activity";
    private static final int REQ_GAME = 100;

    private FrameLayout contentFrame;
    private GameRecordRepository gameRecordRepository;

    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber = 8;
    private long gameStartTime;

    private TextView tvNpcDialog;
    private ImageButton btnPlayNpcDialog;
    // TTS 헬퍼
    private TtsHelper ttsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story8);

        ttsHelper = TtsHelper.getInstance(this);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Story8Activity 시작: " + currentUserName);

        // Repository 초기화
        gameRecordRepository = new GameRecordRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);
        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story8, contentFrame, false); // 레이아웃 재사용
        Button btn = initial.findViewById(R.id.btnStartGame);

        // 뷰 바인딩
        tvNpcDialog = initial.findViewById(R.id.tvNpcDialog);
        btnPlayNpcDialog = initial.findViewById(R.id.btnPlayNpcDialog);

        // 재생 버튼 클릭 → TtsHelper 호출
        btnPlayNpcDialog.setOnClickListener(v ->
                ttsHelper.speak(tvNpcDialog.getText().toString())
        );

        btn.setOnClickListener(v -> {
            gameStartTime = System.currentTimeMillis();

            // Story8에 맞는 게임 (버스 정류장 게임) 시작
            Intent intent = new Intent(Story8Activity.this, BusStopGameActivity.class);
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
            int mistakes = data != null ? data.getIntExtra("mistakes", 0) : 0;

            Log.d(TAG, "✅ Story8 버스 정류장 게임 완료 - 점수: " + score + ", 소요시간: " + (completionTime/1000) + "초");

            // 🎯 새로운 DB 구조에 맞는 게임 기록 저장
            gameRecordRepository.saveGameRecord(
                    currentUserId,
                    currentUserName,
                    currentStoryNumber,
                    "BUS_STOP",
                    score,
                    true, // 성공
                    mistakes,
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
                .inflate(R.layout.activity_end_story8, contentFrame, false); // 레이아웃 재사용
        Button btn = end.findViewById(R.id.btnEnd);

        tvNpcDialog = end.findViewById(R.id.tvNpcDialog);
        btnPlayNpcDialog = end.findViewById(R.id.btnPlayNpcDialog);

        // 재생 버튼 클릭 → TtsHelper 호출
        btnPlayNpcDialog.setOnClickListener(v ->
                ttsHelper.speak(tvNpcDialog.getText().toString())
        );

        btn.setOnClickListener(v -> {
            Log.d(TAG, "Story8 완료 - StoryActivity로 복귀");
            finish();
        });

        contentFrame.addView(end);
    }
}