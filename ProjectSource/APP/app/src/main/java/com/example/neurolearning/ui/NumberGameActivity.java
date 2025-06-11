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

import com.example.neurolearning.R;

public class NumberGameActivity extends AppCompatActivity {
    private static final String TAG = "NumberGameActivity";

    // 키 상수
    public static final String EXTRA_SCORE = "EXTRA_SCORE";

    private TextView tvNpcTalk;
    private TextView tvAnswerNumber;
    private TextView tvFeedback;
    private GridLayout gridChoices;

    private String correctNumber;

    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int mistakeCount = 0;
    private boolean gameCompleted = false;

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

        // 정답으로 표시된 숫자(예: "129")를 가져와서 로컬 변수에 저장
        correctNumber = tvAnswerNumber.getText().toString();

        Log.d(TAG, "정답 숫자: " + correctNumber);

        // GridLayout 안에 들어 있는 9개의 Button을 순회하며 클릭 리스너를 설정
        int childCount = gridChoices.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = gridChoices.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                btn.setOnClickListener(v -> handleChoiceClick(btn));
            }
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
            chosenButton.setBackgroundResource(android.R.color.holo_green_light);

            finishWithResult(true, 100);
        } else {
            tvFeedback.setText("오답입니다… 정답은 " + correctNumber + "이었어요.");
            tvNpcTalk.setText("아쉽지만 괜찮아요! 다음에 더 잘하실 거예요.");

            // 🎯 오답 시 빨간색 배경
            chosenButton.setBackgroundResource(android.R.color.holo_red_light);
            mistakeCount = 1;

            // 정답 버튼을 초록색으로 표시
            highlightCorrectAnswer();

            finishWithResult(false, 50); // 오답 시 50점
        }

        // 선택 후 모든 버튼 비활성화
        disableAllButtons();
    }

    /**
     * 🎯 정답 버튼을 초록색으로 표시
     */
    private void highlightCorrectAnswer() {
        int childCount = gridChoices.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = gridChoices.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                if (btn.getText().toString().equals(correctNumber)) {
                    btn.setBackgroundResource(android.R.color.holo_green_light);
                    break;
                }
            }
        }
    }

    private void disableAllButtons() {
        int childCount = gridChoices.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = gridChoices.getChildAt(i);
            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }
    }

    /**
     * 🎯 게임 결과를 Story5Activity로 반환
     */
    private void finishWithResult(boolean success, int score) {
        Log.d(TAG, "게임 완료 - 성공: " + success + ", 점수: " + score + ", 실수: " + mistakeCount);

        Intent result = new Intent();
        result.putExtra("score", score);
        result.putExtra("mistakes", mistakeCount);
        result.putExtra("success", success);
        result.putExtra(EXTRA_SCORE, score); // 기존 호환성을 위해 유지

        setResult(success ? RESULT_OK : RESULT_CANCELED, result);

        // 2초 뒤 종료하여 사용자가 피드백을 볼 시간 확보
        tvFeedback.postDelayed(this::finish, 2000);
    }
}