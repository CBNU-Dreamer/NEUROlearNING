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
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.view.Window;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.GameRecordRepository;

public class Story9Activity extends AppCompatActivity {
    private static final String TAG = "Story9Activity";
    private static final int REQ_LETTER_GAME = 100;

    private FrameLayout contentFrame;
    private GameRecordRepository gameRecordRepository;

    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber = 9;
    private long gameStartTime;

    private TextView tvNpcDialog;
    private ImageButton btnPlayNpcDialog;
    // TTS 헬퍼
    private TtsHelper ttsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story9);

        ttsHelper = TtsHelper.getInstance(this);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Story9Activity 시작: " + currentUserName);

        // Repository 초기화
        gameRecordRepository = new GameRecordRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);
        showStartScreen();
    }

    /**
     * [1] 스토리9 "시작 화면" 표시
     */
    private void showStartScreen() {
        contentFrame.removeAllViews();
        View startView = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story9, contentFrame, false); // 레이아웃 재사용

        Button btnStartGame = startView.findViewById(R.id.btnStartGame);

        // 뷰 바인딩
        tvNpcDialog = startView.findViewById(R.id.tvNpcDialog);
        btnPlayNpcDialog = startView.findViewById(R.id.btnPlayNpcDialog);

        // 기존에 뷰 바인딩한 이후에 추가:
        ImageView ivNpcImage = startView.findViewById(R.id.ivNpcImage);
        ivNpcImage.setOnClickListener(v -> {
            // Dialog 생성
            final Dialog letterDialog = new Dialog(this);
            letterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            letterDialog.setContentView(R.layout.popup_letter);
            // 배경 투명 처리
            letterDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            // 닫기 버튼 처리
            Button btnClose = letterDialog.findViewById(R.id.btnClosePopup);
            btnClose.setOnClickListener(v2 -> letterDialog.dismiss());

            letterDialog.show();
        });


        // 재생 버튼 클릭 → TtsHelper 호출
        btnPlayNpcDialog.setOnClickListener(v ->
                ttsHelper.speak(tvNpcDialog.getText().toString())
        );

        btnStartGame.setOnClickListener(v -> {
            gameStartTime = System.currentTimeMillis();

            // Story9에 맞는 게임 (편지 선택 게임) 시작
            Intent intent = new Intent(Story9Activity.this, LetterGameActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("userName", currentUserName);
            intent.putExtra("storyNumber", currentStoryNumber);
            startActivityForResult(intent, REQ_LETTER_GAME);
        });

        contentFrame.addView(startView);
    }

    /**
     * [2] LetterGameActivity 완료 후 결과 처리
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_LETTER_GAME && resultCode == RESULT_OK) {
            // 게임 성공 시 DB 업데이트
            long completionTime = System.currentTimeMillis() - gameStartTime;
            int score = data != null ? data.getIntExtra("score", 100) : 100;
            int mistakes = data != null ? data.getIntExtra("mistakes", 0) : 0;

            Log.d(TAG, "✅ Story9 편지 선택 게임 완료 - 점수: " + score + ", 소요시간: " + (completionTime/1000) + "초");

            // 🎯 새로운 DB 구조에 맞는 게임 기록 저장
            gameRecordRepository.saveGameRecord(
                    currentUserId,
                    currentUserName,
                    currentStoryNumber,
                    "LETTER_SELECTION",
                    score,
                    true, // 성공
                    mistakes,
                    completionTime / 1000 // 초 단위
            );

            showEndScreen();
        } else if (requestCode == REQ_LETTER_GAME) {
            Toast.makeText(this, "게임이 정상 종료되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * [3] 스토리9 "종료 화면" 표시
     */
    private void showEndScreen() {
        contentFrame.removeAllViews();
        View endView = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story9, contentFrame, false); // 레이아웃 재사용

        Button btnEnd = endView.findViewById(R.id.btnEnd);

        tvNpcDialog = endView.findViewById(R.id.tvNpcDialog);
        btnPlayNpcDialog = endView.findViewById(R.id.btnPlayNpcDialog);

        // 재생 버튼 클릭 → TtsHelper 호출
        btnPlayNpcDialog.setOnClickListener(v ->
                ttsHelper.speak(tvNpcDialog.getText().toString())
        );

        btnEnd.setOnClickListener(v -> {
            Log.d(TAG, "Story9 완료 - StoryActivity로 복귀");
            finish();
        });

        contentFrame.addView(endView);
    }
}