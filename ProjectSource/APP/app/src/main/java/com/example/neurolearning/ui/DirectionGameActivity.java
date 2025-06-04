package com.example.neurolearning.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectionGameActivity extends AppCompatActivity {

    private static final int SEQUENCE_LENGTH = 7;
    private static final long DISPLAY_INTERVAL_MS = 1000; // 각 화살표 당 1초 간격

    private TextView tvGameTitle;
    private TextView tvInstruction;
    private TextView tvNpcTalk;
    private ImageView ivArrowShow;
    private TextView tvFeedback;
    private LinearLayout layoutUserInput;

    private ImageButton btnUp;
    private ImageButton btnDown;
    private ImageButton btnLeft;
    private ImageButton btnRight;

    private final Handler handler = new Handler();

    private final List<Direction> sequence = new ArrayList<>();
    private final List<Direction> userInput = new ArrayList<>();
    private int displayIndex = 0;
    private boolean acceptingInput = false;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_game);

        // 뷰 바인딩
        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvNpcTalk = findViewById(R.id.tvNpcTalk);
        ivArrowShow = findViewById(R.id.ivArrowShow);
        tvFeedback = findViewById(R.id.tvFeedback);
        layoutUserInput = findViewById(R.id.layoutUserInput);

        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        // 초기 상태: 화살표 표시 영역 숨김, 버튼 비활성화
        ivArrowShow.setVisibility(View.INVISIBLE);
        setButtonsEnabled(false);

        // 랜덤 시퀀스 생성 후 화면에 순차적으로 보여주기
        generateRandomSequence();
        showSequence();

        // 버튼 클릭 리스너 설정
        btnUp.setOnClickListener(v -> handleUserInput(Direction.UP));
        btnDown.setOnClickListener(v -> handleUserInput(Direction.DOWN));
        btnLeft.setOnClickListener(v -> handleUserInput(Direction.LEFT));
        btnRight.setOnClickListener(v -> handleUserInput(Direction.RIGHT));
    }

    /**
     * 1) SEQUENCE_LENGTH만큼 무작위로 방향 리스트를 생성
     */
    private void generateRandomSequence() {
        sequence.clear();
        Random rand = new Random();
        Direction[] values = Direction.values();
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            int idx = rand.nextInt(values.length);
            sequence.add(values[idx]);
        }
    }

    /**
     * 2) sequence 목록을 순차적으로 ivArrowShow에 표시
     */
    private void showSequence() {
        displayIndex = 0;
        tvNpcTalk.setText("강아지가 어디로 갔냐면...");
        tvFeedback.setText("방향을 잘 보고 기억해서 따라 입력하세요!");

        handler.postDelayed(this::displayNextArrow, 500);
    }

    private void displayNextArrow() {
        if (displayIndex < sequence.size()) {
            // 현재 인덱스 화살표 보이기
            Direction dir = sequence.get(displayIndex);
            ivArrowShow.setImageResource(getDrawableForDirection(dir));
            ivArrowShow.setVisibility(View.VISIBLE);

            // 700ms 후 화살표 숨김, 300ms 후 다음 화살표
            handler.postDelayed(() -> {
                ivArrowShow.setVisibility(View.INVISIBLE);
                displayIndex++;
                handler.postDelayed(this::displayNextArrow, 300);
            }, 700);
        } else {
            // 시퀀스 표시가 모두 끝남 → 사용자 입력 허용
            enableUserInput();
        }
    }

    /**
     * 3) 사용자 버튼 입력을 처리
     */
    private void handleUserInput(Direction dir) {
        if (!acceptingInput) return;

        // 시각적으로 layoutUserInput에 작은 ImageView 추가
        ImageView iv = new ImageView(this);
        iv.setImageResource(getDrawableForDirection(dir));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.setMargins(8, 0, 8, 0);
        iv.setLayoutParams(params);
        layoutUserInput.addView(iv);

        userInput.add(dir);

        if (userInput.size() == sequence.size()) {
            // 입력이 모두 끝났으면 채점
            acceptingInput = false;
            setButtonsEnabled(false);
            checkUserAnswer();
        }
    }

    /**
     * 4) 사용자 입력과 정답 비교 후 피드백
     */
    private void checkUserAnswer() {
        boolean allMatch = true;
        for (int i = 0; i < sequence.size(); i++) {
            if (sequence.get(i) != userInput.get(i)) {
                allMatch = false;
                break;
            }
        }

        if (allMatch) {
            tvFeedback.setText("정답입니다! 잘했어요 🐶");
        } else {
            tvFeedback.setText("아쉽네요… 다시 도전해보세요!");
        }

        // (필요시) 몇 초 뒤 재시작하거나 종료할 수 있도록 후속 로직 추가 가능
    }

    /**
     * 5) 사용자 입력을 받을 준비 완료
     */
    private void enableUserInput() {
        tvNpcTalk.setText("이제 방향을 입력해보세요!");
        ivArrowShow.setVisibility(View.INVISIBLE);
        acceptingInput = true;
        setButtonsEnabled(true);
    }

    private void setButtonsEnabled(boolean enabled) {
        btnUp.setEnabled(enabled);
        btnDown.setEnabled(enabled);
        btnLeft.setEnabled(enabled);
        btnRight.setEnabled(enabled);
    }

    /**
     * 방향 enum에 대응하는 drawable 리소스 반환
     */
    private int getDrawableForDirection(Direction dir) {
        switch (dir) {
            case UP:
                return R.drawable.arrow_up_custom;
            case DOWN:
                return R.drawable.arrow_down_custom;
            case LEFT:
                return R.drawable.arrow_left_custom;
            case RIGHT:
                return R.drawable.arrow_right_custom;
            default:
                return android.R.color.transparent;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
