// File: DirectionGameActivity.java
package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class NumberGameActivity extends AppCompatActivity {

    // 키 상수
    public static final String EXTRA_SCORE = "EXTRA_SCORE";

    private TextView tvNpcTalk;
    private TextView tvAnswerNumber;
    private TextView tvFeedback;
    private GridLayout gridChoices;

    private String correctNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XML 파일 이름이 activity_number_memory_game.xml 인 경우:
        setContentView(R.layout.activity_number_game);

        tvNpcTalk       = findViewById(R.id.tvNpcTalk);
        tvAnswerNumber  = findViewById(R.id.tvAnswerNumber);
        tvFeedback      = findViewById(R.id.tvFeedback);
        gridChoices     = findViewById(R.id.gridChoices);

        // 정답으로 표시된 숫자(예: "129")를 가져와서 로컬 변수에 저장
        correctNumber = tvAnswerNumber.getText().toString();

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
        String selected = chosenButton.getText().toString();

        // 정답 여부 판단
        if (selected.equals(correctNumber)) {
            tvFeedback.setText("정답입니다! 🎉");
            finishWithResult(100);
        } else {
            tvFeedback.setText("오답입니다… 다음에 다시 도전하세요.");
            finishWithResult(0);
        }

        // 선택 후 모든 버튼 비활성화
        disableAllButtons();
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
     * 게임 결과(점수)를 호출한 Activity로 되돌려 보냄
     */
    private void finishWithResult(int score) {
        Intent result = new Intent();
        result.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, result);
        // 1초 뒤 종료하여 사용자가 피드백을 볼 시간 확보
        tvFeedback.postDelayed(this::finish, 1000);
    }
}
