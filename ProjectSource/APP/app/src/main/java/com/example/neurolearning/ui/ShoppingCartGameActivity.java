package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartGameActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────────────
    // [1] 레이아웃 컨테이너(암기, 준비, 퀴즈)
    private View layoutMemorize;   // 암기 화면 전체
    private View layoutGetReady;   // 준비 확인 화면 전체
    private ScrollView layoutQuiz; // 퀴즈 화면 전체
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [2] 암기 화면 뷰
    private Button btnStartQuizAfterTimer;
    // CheckBox들은 XML에서 clickable="false"로 지정해 두었으므로,
    // 단순 시각적 표시용입니다.
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [3] 준비 화면 뷰
    private Button btnReadyYes;   // “네, 준비됐어요!”
    private Button btnReadyMore;  // “아직 더 외울래요!”
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [4] 퀴즈 화면 뷰
    private ToggleButton tbOpt1, tbOpt2, tbOpt3, tbOpt4, tbOpt5, tbOpt6;
    private TextView tvSelectedItems;
    private TextView tvQuizResult;
    private Button btnSubmitQuiz;
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [5] 정답 리스트 (암기해야 할 물건 순서대로)
    private final List<String> correctList = new ArrayList<>();

    // [6] 사용자가 선택한 순서 (토글 버튼을 누른 순서대로 순서를 유지)
    private final List<String> userSelected = new ArrayList<>();
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // DB/진행 기록 관련
    private GameProgressRepository gameProgressRepository;
    private String currentUsername;
    private int currentStoryNumber;
    private long gameStartTime;
    private int totalAttempts = 0;   // 퀴즈 제출 시도 횟수
    private int correctAttempts = 0; // 정답 맞춘 횟수 (퀴즈는 한 번만 제출하므로 1 또는 0)
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_game);

        // ─────────────────────────────────────────────────────────────
        // (A) 사용자 정보 및 DB Repository 초기화
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "testuser";
        }
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 1);

        gameProgressRepository = new GameProgressRepository(getApplication());
        gameStartTime = System.currentTimeMillis();
        // ─────────────────────────────────────────────────────────────

        // ─────────────────────────────────────────────────────────────
        // 1) 레이아웃 컨테이너 바인딩
        layoutMemorize = findViewById(R.id.layoutMemorize);
        layoutGetReady = findViewById(R.id.layoutGetReady);
        layoutQuiz = findViewById(R.id.layoutQuiz);

        // 2) 암기 화면 뷰 바인딩
        btnStartQuizAfterTimer = findViewById(R.id.btnStartQuizAfterTimer);

        // 3) 준비 화면 뷰 바인딩
        btnReadyYes = findViewById(R.id.btnReadyYes);
        btnReadyMore = findViewById(R.id.btnReadyMore);

        // 4) 퀴즈 화면 뷰 바인딩
        tbOpt1 = findViewById(R.id.tb_opt1);
        tbOpt2 = findViewById(R.id.tb_opt2);
        tbOpt3 = findViewById(R.id.tb_opt3);
        tbOpt4 = findViewById(R.id.tb_opt4);
        tbOpt5 = findViewById(R.id.tb_opt5);
        tbOpt6 = findViewById(R.id.tb_opt6);

        tvSelectedItems = findViewById(R.id.tvSelectedItems);
        tvQuizResult = findViewById(R.id.tvQuizResult);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        // ─────────────────────────────────────────────────────────────

        // ─────────────────────────────────────────────────────────────
        // [7] 정답 리스트 초기화
        correctList.add("칫솔");
        correctList.add("치약");
        correctList.add("샤인머스캣");
        correctList.add("물");
        // 필요하다면 아이템을 더 추가하거나 동적으로 처리 가능
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

        // (D) 퀴즈 옵션 토글 버튼 리스너
        View.OnClickListener quizOptionListener = v -> {
            ToggleButton tb = (ToggleButton) v;
            String item = tb.getText().toString();
            if (tb.isChecked()) {
                userSelected.add(item);
            } else {
                userSelected.remove(item);
            }
            updateSelectedItemsText();
        };
        tbOpt1.setOnClickListener(quizOptionListener);
        tbOpt2.setOnClickListener(quizOptionListener);
        tbOpt3.setOnClickListener(quizOptionListener);
        tbOpt4.setOnClickListener(quizOptionListener);
        tbOpt5.setOnClickListener(quizOptionListener);
        tbOpt6.setOnClickListener(quizOptionListener);

        // (E) 퀴즈 제출 버튼 리스너
        btnSubmitQuiz.setOnClickListener(v -> {
            totalAttempts++;
            checkQuizAnswer();
        });
        // ─────────────────────────────────────────────────────────────
    }

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
        tbOpt1.setChecked(false);
        tbOpt2.setChecked(false);
        tbOpt3.setChecked(false);
        tbOpt4.setChecked(false);
        tbOpt5.setChecked(false);
        tbOpt6.setChecked(false);

        userSelected.clear();
        tvSelectedItems.setText("선택된 물건: ");
        tvQuizResult.setText("");
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [D] 사용자가 선택한 목록을 TextView에 업데이트
    private void updateSelectedItemsText() {
        StringBuilder sb = new StringBuilder("선택된 물건: ");
        for (int i = 0; i < userSelected.size(); i++) {
            sb.append(userSelected.get(i));
            if (i < userSelected.size() - 1) {
                sb.append(", ");
            }
        }
        tvSelectedItems.setText(sb.toString());
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [E] 퀴즈 정답 검사 및 게임 완료 처리
    private void checkQuizAnswer() {
        // (1) 물건 개수 비교
        if (userSelected.size() != correctList.size()) {
            tvQuizResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvQuizResult.setText("물건 개수가 다릅니다. 다시 확인하세요!");
            return;
        }

        // (2) 순서 및 내용 비교
        boolean allMatch = true;
        for (int i = 0; i < correctList.size(); i++) {
            if (!correctList.get(i).equals(userSelected.get(i))) {
                allMatch = false;
                break;
            }
        }

        if (allMatch) {
            correctAttempts = 1;
            tvQuizResult.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvQuizResult.setText("정답입니다! 잘했어요 🎉");
            // 게임 완료 로직 호출 (잠시 후 결과 저장 및 종료)
            new Handler().postDelayed(this::completeGame, 1000);
        } else {
            tvQuizResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvQuizResult.setText("다시 한 번 도전해 보세요!");
        }
    }
    // ─────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────
    // [F] 게임 완료 처리
    private void completeGame() {
        // (1) UI 비활성화
        tbOpt1.setEnabled(false);
        tbOpt2.setEnabled(false);
        tbOpt3.setEnabled(false);
        tbOpt4.setEnabled(false);
        tbOpt5.setEnabled(false);
        tbOpt6.setEnabled(false);
        btnSubmitQuiz.setEnabled(false);
        tvQuizResult.setText("게임 완료! 결과를 저장 중입니다...");

        // (2) 점수 계산
        // 실수 횟수 = 총 시도 횟수 - 정답 맞춘 횟수
        int mistakes = totalAttempts - correctAttempts;
        int baseScore = 100;
        int finalScore = Math.max(baseScore - (mistakes * 10), 10);
        // 예: 오답 1번당 10점 감점, 최소 10점 보장

        long completionTimeSec = (System.currentTimeMillis() - gameStartTime) / 1000;

        // (3) DB에 게임 기록 저장
        gameProgressRepository.saveGamePlayRecord(
                currentUsername,
                currentStoryNumber,
                "SHOPPING_CART",   // 게임 타입 식별자
                finalScore,
                true,              // 성공 여부 (맞춰서 성공)
                mistakes,
                completionTimeSec
        );

        // (4) 잠시 후 결과를 Story1Activity로 전달하고 종료
        new Handler().postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("score", finalScore);
            resultIntent.putExtra("completionTime", completionTimeSec);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 1500);
    }
    // ─────────────────────────────────────────────────────────────────
}
