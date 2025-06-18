package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NumberGameActivity extends AppCompatActivity {
    private static final String TAG = "NumberGameActivity";

    // 키 상수
    public static final String EXTRA_SCORE = "EXTRA_SCORE";

    private TextView tvNpcTalk;
    private TextView tvAnswerNumber;
    private TextView tvFeedback;
    private GridLayout gridChoices;

    private String correctNumber;
    private final List<Button> choiceButtons = new ArrayList<>();

    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int mistakeCount = 0;
    private boolean gameCompleted = false;
    private int attemptCount = 0; // 시도 횟수 추가

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_game);

        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 5);

        Log.d(TAG, "숫자 기억 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        tvNpcTalk       = findViewById(R.id.tvNpcTalk);
        tvAnswerNumber  = findViewById(R.id.tvAnswerNumber);
        tvFeedback      = findViewById(R.id.tvFeedback);
        gridChoices     = findViewById(R.id.gridChoices);

        // 🎯 첫 번째 게임 시작
        startNewGame();
    }

    /**
     * 🎯 새로운 게임을 시작하는 메서드
     */
    private void startNewGame() {
        attemptCount++;
        gameCompleted = false;

        Log.d(TAG, "새 게임 시작 - 시도 횟수: " + attemptCount);

        // 🎯 UI 초기화
        tvNpcTalk.setText(attemptCount == 1 ?
                "숫자를 잘 기억해 주세요!" :
                "다시 한 번! 숫자를 잘 기억해 주세요!");

        tvFeedback.setVisibility(View.GONE);

        // 모든 버튼 색상 초기화
        resetButtonColors();

        // 🎯 랜덤 숫자 게임 생성
        generateRandomNumbers();

        // ▶ 초깃값: 숫자만 보이고, 버튼·피드백 숨김, 버튼 비활성화
        tvAnswerNumber.setVisibility(View.VISIBLE);
        gridChoices.setVisibility(View.GONE);
        tvFeedback.setVisibility(View.GONE);
        disableAllButtons();

        // ▶ 3초 뒤에 숫자 사라지고 버튼·피드백 보이기
        tvAnswerNumber.postDelayed(() -> {
            tvAnswerNumber.setVisibility(View.GONE);
            gridChoices.setVisibility(View.VISIBLE);
            tvFeedback.setVisibility(View.VISIBLE);
            enableAllButtons();
        }, 3000);

        Log.d(TAG, "정답 숫자: " + correctNumber);

        // 버튼들에 클릭 리스너 설정
        setupButtonListeners();
    }

    /**
     * 🎯 모든 버튼 색상을 기본값으로 초기화
     */
    private void resetButtonColors() {
        for (Button btn : choiceButtons) {
            btn.setBackgroundTintList(null); // 기본 색상으로 복원
        }
    }

    /**
     * 🎯 랜덤 3자리 숫자 9개 생성 및 정답 선택
     */
    private void generateRandomNumbers() {
        Random random = new Random();
        Set<String> numberSet = new HashSet<>();

        // 중복되지 않는 3자리 숫자 9개 생성
        while (numberSet.size() < 9) {
            int number = random.nextInt(900) + 100; // 100~999 사이의 3자리 숫자
            numberSet.add(String.valueOf(number));
        }

        // Set을 List로 변환
        List<String> numberList = new ArrayList<>(numberSet);

        // 🎯 정답을 먼저 랜덤하게 선택
        correctNumber = numberList.get(random.nextInt(numberList.size()));

        // 정답 숫자를 화면에 표시
        tvAnswerNumber.setText(correctNumber);

        // 🎯 버튼 배치를 위해 리스트를 다시 섞기
        Collections.shuffle(numberList);

        // 선택지 버튼들에 숫자 할당
        assignNumbersToButtons(numberList);

        Log.d(TAG, "🎯 생성된 랜덤 숫자들: " + numberList.toString());
        Log.d(TAG, "🎯 정답: " + correctNumber);
    }

    /**
     * 🎯 생성된 숫자들을 버튼에 할당
     */
    private void assignNumbersToButtons(List<String> numbers) {
        choiceButtons.clear();

        // GridLayout의 모든 버튼을 찾아서 숫자 할당
        int childCount = gridChoices.getChildCount();
        int numberIndex = 0;

        for (int i = 0; i < childCount && numberIndex < numbers.size(); i++) {
            View child = gridChoices.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                btn.setText(numbers.get(numberIndex));
                choiceButtons.add(btn);
                numberIndex++;
            }
        }

        Log.d(TAG, "버튼에 숫자 할당 완료: " + choiceButtons.size() + "개");
    }

    /**
     * 🎯 버튼 클릭 리스너 설정
     */
    private void setupButtonListeners() {
        for (Button btn : choiceButtons) {
            btn.setOnClickListener(v -> handleChoiceClick(btn));
        }
    }

    private void enableAllButtons() {
        for (Button btn : choiceButtons) {
            btn.setEnabled(true);
        }
    }

    /**
     * 사용자가 숫자 버튼을 눌렀을 때 호출됨
     */
    private void handleChoiceClick(Button chosenButton) {
        if (gameCompleted) return;

        String selected = chosenButton.getText().toString();
        gameCompleted = true;

        Log.d(TAG, "사용자 선택: " + selected + ", 정답: " + correctNumber);

        // 정답 여부 판단
        if (selected.equals(correctNumber)) {
            tvFeedback.setText("정답입니다! 🎉");
            tvNpcTalk.setText("와! 정말 잘 기억하셨네요!");

            // 🎯 정답 시 초록색 배경
            chosenButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, android.R.color.holo_green_light)
            );

            // 선택 후 모든 버튼 비활성화
            disableAllButtons();

            // 🎯 정답 시에만 게임 종료
            finishWithResult(true, 100);
        } else {
            // 🎯 오답 시 재시작을 위한 처리
            tvFeedback.setText("아쉬워요! 정답은 " + correctNumber + "이었어요.");
            tvNpcTalk.setText("괜찮아요! 성공할 때까지 계속 해봐요! 💪");

            // 🎯 오답 시 빨간색 배경
            chosenButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, android.R.color.holo_red_light)
            );
            mistakeCount++;

            // 정답 버튼을 초록색으로 표시
            highlightCorrectAnswer();

            // 선택 후 모든 버튼 비활성화
            disableAllButtons();

            // 🎯 3초 후 새 게임 시작
            tvFeedback.postDelayed(() -> {
                startNewGame();
            }, 3000);
        }
    }

    /**
     * 🎯 정답 버튼을 초록색으로 표시
     */
    private void highlightCorrectAnswer() {
        for (Button btn : choiceButtons) {
            if (btn.getText().toString().equals(correctNumber)) {
                btn.setBackgroundTintList(
                        ContextCompat.getColorStateList(this, android.R.color.holo_green_light)
                );
                break;
            }
        }
    }

    private void disableAllButtons() {
        for (Button btn : choiceButtons) {
            btn.setEnabled(false);
        }
    }

    /**
     * 🎯 게임 결과를 Story5Activity로 반환 (정답일 때만 호출)
     */
    private void finishWithResult(boolean success, int score) {
        Log.d(TAG, "게임 완료 - 성공: " + success + ", 점수: " + score + ", 실수: " + mistakeCount + ", 시도: " + attemptCount);

        Intent result = new Intent();
        result.putExtra("score", score);
        result.putExtra("mistakes", mistakeCount);
        result.putExtra("success", success);
        result.putExtra("attempts", attemptCount); // 시도 횟수 추가
        result.putExtra(EXTRA_SCORE, score); // 기존 호환성을 위해 유지

        setResult(success ? RESULT_OK : RESULT_CANCELED, result);

        // 2초 뒤 종료하여 사용자가 피드백을 볼 시간 확보
        tvFeedback.postDelayed(this::finish, 2000);
    }
}