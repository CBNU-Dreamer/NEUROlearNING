package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShoppingCartGameActivity extends AppCompatActivity {
    private static final String TAG = "ShoppingCartGameActivity";

    // ─────────────────────────────────────────────────────────────────
    // [1] 레이아웃 컨테이너(암기, 준비, 퀴즈)
    private View layoutMemorize;   // 암기 화면 전체
    private View layoutGetReady;   // 준비 확인 화면 전체
    private ScrollView layoutQuiz; // 퀴즈 화면 전체
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [2] 암기 화면 뷰
    private Button btnStartQuizAfterTimer;
    private LinearLayout llItemList; // 체크박스들이 들어갈 컨테이너
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [3] 준비 화면 뷰
    private Button btnReadyYes;   // "네, 준비됐어요!"
    private Button btnReadyMore;  // "아직 더 외울래요!"
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [4] 퀴즈 화면 뷰
    private LinearLayout llQuizOptions; // 토글버튼들이 들어갈 컨테이너
    private final List<ToggleButton> quizButtons = new ArrayList<>();
    private TextView tvSelectedItems;
    private TextView tvQuizResult;
    private Button btnSubmitQuiz;
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [5] 🎯 랜덤 아이템 풀 및 정답 관리
    private final List<String> allItems = Arrays.asList(
            "칫솔", "치약", "샤인머스캣", "물", "음료", "과자",
            "빵", "우유", "사과", "바나나", "라면", "계란",
            "쌀", "김치", "고기", "생선", "야채", "과일"
    );

    private final List<String> correctAnswers = new ArrayList<>(); // 암기해야 할 4개 아이템
    private final Set<String> userSelected = new HashSet<>();     // 사용자가 선택한 아이템들 (순서 무관)
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private long gameStartTime;
    private int totalAttempts = 0;   // 퀴즈 제출 시도 횟수
    private int correctAttempts = 0; // 정답 맞춘 횟수
    private boolean gameCompleted = false;
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_game);

        // ─────────────────────────────────────────────────────────────
        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 6);

        gameStartTime = System.currentTimeMillis();

        Log.d(TAG, "장보기 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");
        // ─────────────────────────────────────────────────────────────

        // ─────────────────────────────────────────────────────────────
        // 1) 레이아웃 컨테이너 바인딩
        layoutMemorize = findViewById(R.id.layoutMemorize);
        layoutGetReady = findViewById(R.id.layoutGetReady);
        layoutQuiz = findViewById(R.id.layoutQuiz);

        // 2) 암기 화면 뷰 바인딩
        btnStartQuizAfterTimer = findViewById(R.id.btnStartQuizAfterTimer);
        llItemList = findViewById(R.id.llItemList);

        // 3) 준비 화면 뷰 바인딩
        btnReadyYes = findViewById(R.id.btnReadyYes);
        btnReadyMore = findViewById(R.id.btnReadyMore);

        // 4) 퀴즈 화면 뷰 바인딩
        llQuizOptions = findViewById(R.id.llQuizOptions);
        tvSelectedItems = findViewById(R.id.tvSelectedItems);
        tvQuizResult = findViewById(R.id.tvQuizResult);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        // ─────────────────────────────────────────────────────────────

        // ─────────────────────────────────────────────────────────────
        // [6] 🎯 랜덤 게임 초기화
        generateRandomGame();
        // ─────────────────────────────────────────────────────────────

        // ─────────────────────────────────────────────────────────────
        // (B) 암기 화면 타이머 시작
        startMemorizeTimer();

        // (C) 준비 화면 버튼 리스너
        btnReadyYes.setOnClickListener(v -> showQuizScreen());
        btnReadyMore.setOnClickListener(v -> {
            showMemorizeScreen();
            startMemorizeTimer();
        });

        // (D) 퀴즈 제출 버튼 리스너
        btnSubmitQuiz.setOnClickListener(v -> {
            if (gameCompleted) return;

            totalAttempts++;
            Log.d(TAG, "퀴즈 제출 시도 #" + totalAttempts + ", 선택: " + userSelected.toString());
            checkQuizAnswer();
        });
        // ─────────────────────────────────────────────────────────────
    }

    // ─────────────────────────────────────────────────────────────────
    // [🎯 NEW] 랜덤 게임 생성
    private void generateRandomGame() {
        // (1) 전체 아이템에서 랜덤하게 4개 선택
        List<String> shuffledItems = new ArrayList<>(allItems);
        Collections.shuffle(shuffledItems);

        correctAnswers.clear();
        for (int i = 0; i < 4; i++) {
            correctAnswers.add(shuffledItems.get(i));
        }

        Log.d(TAG, "🎯 랜덤 정답 아이템들: " + correctAnswers.toString());

        // (2) 암기 화면에 동적으로 체크박스 생성
        createMemorizeCheckboxes();

        // (3) 퀴즈 화면에 동적으로 토글버튼 생성 (정답 4개 + 오답 4개)
        createQuizButtons();
    }

    // ─────────────────────────────────────────────────────────────────
    // [🎯 NEW] 암기 화면 체크박스 동적 생성
    private void createMemorizeCheckboxes() {
        llItemList.removeAllViews(); // 기존 뷰들 제거

        for (int i = 0; i < correctAnswers.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(correctAnswers.get(i));
            checkBox.setTextSize(16);
            checkBox.setClickable(false);
            checkBox.setFocusable(false);

            // 첫 번째 아이템만 체크된 상태로 시작
            checkBox.setChecked(i == 0);

            llItemList.addView(checkBox);
        }

        Log.d(TAG, "암기 화면 체크박스 " + correctAnswers.size() + "개 생성 완료");
    }

    // ─────────────────────────────────────────────────────────────────
    // [🎯 NEW] 퀴즈 화면 토글버튼 동적 생성
    private void createQuizButtons() {
        llQuizOptions.removeAllViews();
        quizButtons.clear();

        // (1) 정답 4개 + 랜덤 오답 4개 = 총 8개 선택지
        List<String> quizOptions = new ArrayList<>(correctAnswers);

        // (2) 오답 4개 추가
        List<String> remainingItems = new ArrayList<>(allItems);
        remainingItems.removeAll(correctAnswers); // 정답 제외
        Collections.shuffle(remainingItems);

        for (int i = 0; i < 4 && i < remainingItems.size(); i++) {
            quizOptions.add(remainingItems.get(i));
        }

        // (3) 선택지 섞기
        Collections.shuffle(quizOptions);

        // (4) 토글버튼 생성
        for (String item : quizOptions) {
            ToggleButton toggleButton = new ToggleButton(this);
            toggleButton.setTextOff(item);
            toggleButton.setTextOn(item);

            // 레이아웃 파라미터 설정
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (48 * getResources().getDisplayMetrics().density) // 48dp를 px로 변환
            );
            params.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
            toggleButton.setLayoutParams(params);

            // 클릭 리스너 설정
            toggleButton.setOnClickListener(v -> {
                if (gameCompleted) return;

                ToggleButton tb = (ToggleButton) v;
                String selectedItem = tb.getText().toString();

                if (tb.isChecked()) {
                    userSelected.add(selectedItem);
                    Log.d(TAG, "아이템 선택: " + selectedItem);
                } else {
                    userSelected.remove(selectedItem);
                    Log.d(TAG, "아이템 선택 해제: " + selectedItem);
                }
                updateSelectedItemsText();
            });

            llQuizOptions.addView(toggleButton);
            quizButtons.add(toggleButton);
        }

        Log.d(TAG, "퀴즈 화면 토글버튼 " + quizOptions.size() + "개 생성 완료");
        Log.d(TAG, "퀴즈 선택지: " + quizOptions.toString());
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [A] 화면 전환 메서드들
    private void showMemorizeScreen() {
        layoutMemorize.setVisibility(View.VISIBLE);
        layoutGetReady.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.GONE);

        resetQuizState();
    }

    private void showGetReadyScreen() {
        layoutMemorize.setVisibility(View.GONE);
        layoutGetReady.setVisibility(View.VISIBLE);
        layoutQuiz.setVisibility(View.GONE);
    }

    private void showQuizScreen() {
        layoutMemorize.setVisibility(View.GONE);
        layoutGetReady.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.VISIBLE);

        resetQuizState();
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [B] 암기 화면에서 60초 타이머 시작
    private void startMemorizeTimer() {
        // 버튼을 눌러도 즉시 준비 화면으로 전환
        btnStartQuizAfterTimer.setOnClickListener(v -> showGetReadyScreen());

        new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                btnStartQuizAfterTimer.setText(secondsLeft + "초 뒤 장보기 시작!");
            }

            @Override
            public void onFinish() {
                showGetReadyScreen();
            }
        }.start();
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [C] 퀴즈 화면 초기화
    private void resetQuizState() {
        // 모든 토글버튼 초기화
        for (ToggleButton button : quizButtons) {
            button.setChecked(false);
        }

        userSelected.clear();
        tvSelectedItems.setText("선택된 물건: ");
        tvQuizResult.setText("");
        gameCompleted = false;
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [D] 사용자가 선택한 목록을 TextView에 업데이트
    private void updateSelectedItemsText() {
        StringBuilder sb = new StringBuilder("선택된 물건: ");
        List<String> selectedList = new ArrayList<>(userSelected);
        for (int i = 0; i < selectedList.size(); i++) {
            sb.append(selectedList.get(i));
            if (i < selectedList.size() - 1) {
                sb.append(", ");
            }
        }
        tvSelectedItems.setText(sb.toString());
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [E] 🎯 퀴즈 정답 검사 (순서 무관, 개수와 내용만 확인)
    private void checkQuizAnswer() {
        // (1) 물건 개수 비교
        if (userSelected.size() != correctAnswers.size()) {
            tvQuizResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            tvQuizResult.setText("물건 개수가 다릅니다. " + correctAnswers.size() + "개를 선택하세요!");
            Log.d(TAG, "개수 불일치: 선택 " + userSelected.size() + "개, 정답 " + correctAnswers.size() + "개");
            return;
        }

        // (2) 🎯 순서 무관하게 내용만 비교 (Set 사용)
        Set<String> correctSet = new HashSet<>(correctAnswers);
        Set<String> userSet = new HashSet<>(userSelected);

        if (correctSet.equals(userSet)) {
            correctAttempts = 1;
            gameCompleted = true;

            tvQuizResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            tvQuizResult.setText("정답입니다! 모든 물건을 정확히 기억하셨네요! 🎉");

            Log.d(TAG, "✅ 게임 성공! 시도 횟수: " + totalAttempts);

            // 🎯 게임 완료 로직 호출 (잠시 후 결과 저장 및 종료)
            new Handler().postDelayed(this::completeGame, 1500);
        } else {
            tvQuizResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            tvQuizResult.setText("다시 한 번 생각해 보세요! 정답: " + correctAnswers.toString());
            Log.d(TAG, "❌ 오답, 재시도 가능");
            Log.d(TAG, "정답: " + correctSet + ", 선택: " + userSet);
        }
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [F] 🎯 게임 완료 처리 (새로운 DB 구조 대응)
    private void completeGame() {
        // (1) UI 비활성화
        setAllButtonsEnabled(false);
        tvQuizResult.setText("게임 완료! 결과를 저장 중입니다...");

        // (2) 점수 계산
        int mistakes = totalAttempts - correctAttempts; // 실수 횟수 = 총 시도 - 성공 횟수
        int baseScore = 100;
        int finalScore = Math.max(baseScore - (mistakes * 10), 10); // 실수 1회당 10점 감점, 최소 10점

        Log.d(TAG, "점수 계산: 기본 " + baseScore + "점, 실수 " + mistakes + "회, 최종 " + finalScore + "점");

        // (3) 🎯 Story6Activity로 결과 반환 (GameRecordRepository는 Story6Activity에서 처리)
        new Handler().postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("score", finalScore);
            resultIntent.putExtra("mistakes", mistakes);
            resultIntent.putExtra("success", true);

            setResult(RESULT_OK, resultIntent);

            Log.d(TAG, "게임 결과 반환 완료");
            finish();
        }, 1500);
    }

    /**
     * 🎯 모든 버튼 활성화/비활성화
     */
    private void setAllButtonsEnabled(boolean enabled) {
        for (ToggleButton button : quizButtons) {
            button.setEnabled(enabled);
        }
        btnSubmitQuiz.setEnabled(enabled);
    }
    // ─────────────────────────────────────────────────────────────────
}