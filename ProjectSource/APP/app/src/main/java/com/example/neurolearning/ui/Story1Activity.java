package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.GameProgressRepository; // 추가

import java.util.Locale;

public class Story1Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int REQ_GAME = 100; // 게임 요청 코드 (Story1에 맞는 게임으로 변경 가능)
    private FrameLayout contentFrame;

    // DB 관련 추가
    private GameProgressRepository gameProgressRepository;
    private String currentUsername;
    private int currentStoryNumber = 1; // Story1Activity이므로 1번 스토리
    private long gameStartTime;

    // TTS 관련
    private TextToSpeech tts;
    private TextView tvNpcDialog;          // NPC 대화창 텍스트뷰
    private ImageButton btnPlayNpcDialog;  // 음성 재생 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story1); // Story1 레이아웃

        // 사용자 정보 가져오기 (추가)
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "testuser"; // 임시값
        }

        // Repository 초기화 (추가)
        gameProgressRepository = new GameProgressRepository(getApplication());

        contentFrame = findViewById(R.id.contentFrame);

        // TTS 초기화
        tts = new TextToSpeech(this, this);

        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story1, contentFrame, false); // Story1 시작 레이아웃

        // NPC 대화창 텍스트뷰 바인딩
        tvNpcDialog = initial.findViewById(R.id.tvNpcDialog);
        // 새로 추가된 음성 재생 버튼 바인딩
        btnPlayNpcDialog = initial.findViewById(R.id.btnPlayNpcDialog);

        // 음성 재생 버튼 클릭 시 TTS를 통해 대화창 텍스트 읽어주기
        btnPlayNpcDialog.setOnClickListener(v -> {
            String textToSpeak = tvNpcDialog.getText().toString();
            speakText(textToSpeak);
        });

        // 게임 시작 버튼 바인딩
        Button btn = initial.findViewById(R.id.btnStartGame);
        btn.setOnClickListener(v -> {
            gameStartTime = System.currentTimeMillis(); // 게임 시작 시간 기록 (추가)

            // Story1에 맞는 게임 Activity 호출 (예시: 십자말 풀이)
            Intent intent = new Intent(Story1Activity.this, CrossWordGameActivity.class);
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
                    "STORY1_GAME", // Story1에 맞는 게임 타입으로 변경
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
                .inflate(R.layout.activity_end_story1, contentFrame, false); // Story1 종료 레이아웃
        Button btn = end.findViewById(R.id.btnEnd);

        btn.setOnClickListener(v -> finish());
        contentFrame.addView(end);
    }

    // ─────────────────────────────────────────────────────────────────
    // TextToSpeech.OnInitListener 메서드
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 한국어로 설정
            int result = tts.setLanguage(Locale.KOREAN);
            // 목소리 톤을 살짝 높여서(1.2f) 여성 음성 느낌을 줌
            tts.setPitch(1.2f);
            tts.setSpeechRate(1.0f);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS: 해당 언어를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TTS 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 텍스트를 음성으로 읽어주는 헬퍼 메서드
     */
    private void speakText(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NPC_DIALOG");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 종료 및 자원 해제
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    // ─────────────────────────────────────────────────────────────────

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 액션바 뒤로가기 처리
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
