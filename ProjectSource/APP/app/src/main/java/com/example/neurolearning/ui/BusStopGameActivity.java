package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BusStopGameActivity extends AppCompatActivity {
    private static final String TAG = "BusStopGameActivity";

    // UI 컴포넌트
    private TextView tvNpcDialogue;
    private TextView tvBusLineDisplay;
    private LinearLayout busLineContainer;
    private Button btnShowBusLine;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;

    // 게임 데이터
    private final String[] busStops = {"서울역", "시청", "종로3가", "동대문", "신설동", "청량리"};
    private String currentDestination;
    private int correctAnswer;
    private boolean busLineVisible = false;
    private int gamePhase = 1; // 1: 목적지 제시, 2: 노선표 보기, 3: 답변 대기

    // 게임 상태
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int mistakeCount = 0;
    private boolean gameCompleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busstop_game_new);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 8);

        Log.d(TAG, "버스 정류장 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        initViews();
        startNewRound();
    }

    private void initViews() {
        tvNpcDialogue = findViewById(R.id.tvNpcDialogue);
        tvBusLineDisplay = findViewById(R.id.tvBusLineDisplay);
        busLineContainer = findViewById(R.id.busLineContainer);
        btnShowBusLine = findViewById(R.id.btnShowBusLine);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);

        // 노선표 보기 버튼 클릭
        btnShowBusLine.setOnClickListener(v -> showBusLine());

        // 답변 버튼들 클릭 리스너
        View.OnClickListener answerListener = v -> {
            if (gamePhase != 3) return;

            Button clicked = (Button) v;
            int chosenNumber = Integer.parseInt(clicked.getText().toString());
            checkAnswer(chosenNumber);
        };

        btn1.setOnClickListener(answerListener);
        btn2.setOnClickListener(answerListener);
        btn3.setOnClickListener(answerListener);
        btn4.setOnClickListener(answerListener);
        btn5.setOnClickListener(answerListener);
        btn6.setOnClickListener(answerListener);
    }

    /**
     * 새로운 라운드 시작 - 랜덤 목적지 선택
     */
    private void startNewRound() {
        // 출발지(서울역) 제외하고 랜덤 목적지 선택
        List<String> destinations = new ArrayList<>(Arrays.asList(busStops));
        destinations.remove(0); // 서울역 제거

        Random random = new Random();
        currentDestination = destinations.get(random.nextInt(destinations.size()));

        // 정답 계산 (서울역부터 목적지까지의 정류장 수)
        correctAnswer = Arrays.asList(busStops).indexOf(currentDestination);

        Log.d(TAG, "새 라운드 - 목적지: " + currentDestination + ", 정답: " + correctAnswer);

        // UI 초기화
        gamePhase = 1;
        busLineVisible = false;
        busLineContainer.setVisibility(View.GONE);
        btnShowBusLine.setVisibility(View.VISIBLE);

        // NPC 대사 설정
        tvNpcDialogue.setText("저는 " + currentDestination + "에서 내려야 해요!");

        // 답변 버튼들 활성화
        enableAnswerButtons(false);
    }

    /**
     * 버스 노선표 보기
     */
    private void showBusLine() {
        if (gamePhase != 1) return;

        gamePhase = 2;
        busLineVisible = true;

        // 노선표 표시
        busLineContainer.setVisibility(View.VISIBLE);
        btnShowBusLine.setVisibility(View.GONE);

        // 노선표 텍스트 구성
        StringBuilder busLine = new StringBuilder();
        for (int i = 0; i < busStops.length; i++) {
            busLine.append(busStops[i]);
            if (i < busStops.length - 1) {
                busLine.append(" → ");
            }
        }
        tvBusLineDisplay.setText(busLine.toString());

        // 1초 후 질문 단계로 이동
        tvNpcDialogue.postDelayed(() -> {
            if (gamePhase == 2) {
                gamePhase = 3;
                tvNpcDialogue.setText("엇? 제가 몇 정류장 뒤에 내려야 하는 거죠?");
                enableAnswerButtons(true);
            }
        }, 1000);
    }

    /**
     * 답변 확인
     */
    private void checkAnswer(int chosenNumber) {
        if (gameCompleted) return;

        Log.d(TAG, "사용자 선택: " + chosenNumber + ", 정답: " + correctAnswer);

        if (chosenNumber == correctAnswer) {
            // 정답!
            Toast.makeText(this, "정답입니다! 🎉", Toast.LENGTH_LONG).show();
            Log.d(TAG, "✅ 정답!");

            gameCompleted = true;
            finishWithResult(true, 100);

        } else {
            // 오답
            mistakeCount++;
            Toast.makeText(this, "앗 잘못 내렸어요 ㅠㅠ 이번에는 잘 내려봐요", Toast.LENGTH_LONG).show();
            Log.d(TAG, "❌ 오답 - 실수 횟수: " + mistakeCount);

            // 새로운 라운드 시작
            tvNpcDialogue.postDelayed(() -> {
                if (!gameCompleted) {
                    startNewRound();
                }
            }, 2000);
        }

        enableAnswerButtons(false);
    }

    /**
     * 답변 버튼들 활성화/비활성화
     */
    private void enableAnswerButtons(boolean enabled) {
        btn1.setEnabled(enabled);
        btn2.setEnabled(enabled);
        btn3.setEnabled(enabled);
        btn4.setEnabled(enabled);
        btn5.setEnabled(enabled);
        btn6.setEnabled(enabled);

        // 시각적 표시
        float alpha = enabled ? 1.0f : 0.5f;
        btn1.setAlpha(alpha);
        btn2.setAlpha(alpha);
        btn3.setAlpha(alpha);
        btn4.setAlpha(alpha);
        btn5.setAlpha(alpha);
        btn6.setAlpha(alpha);
    }

    /**
     * 게임 결과 반환
     */
    private void finishWithResult(boolean success, int score) {
        Log.d(TAG, "게임 완료 - 성공: " + success + ", 점수: " + score + ", 실수: " + mistakeCount);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("mistakes", mistakeCount);
        resultIntent.putExtra("success", success);

        setResult(success ? RESULT_OK : RESULT_CANCELED, resultIntent);

        // 2초 후 종료
        findViewById(android.R.id.content).postDelayed(this::finish, 2000);
    }
}