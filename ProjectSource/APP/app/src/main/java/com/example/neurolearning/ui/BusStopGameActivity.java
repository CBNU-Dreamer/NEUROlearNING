package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class BusStopGameActivity extends AppCompatActivity {
    private static final String TAG = "BusStopGameActivity";

    private TextView tvTargetHint;
    private TextView tvBusLine;
    private TextView tvQuestion;

    private Button btn1, btn2, btn3, btn4, btn5, btn6;

    // 정답 정류장 인덱스 (0부터 카운트)
    private int correctPosition;

    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int mistakeCount = 0;
    private boolean gameCompleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busstop_game);

        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 8);

        Log.d(TAG, "버스 정류장 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        // 뷰 바인딩
        tvTargetHint = findViewById(R.id.tvTargetHint);
        tvBusLine     = findViewById(R.id.tvBusLine);
        tvQuestion    = findViewById(R.id.tvQuestion);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);

        // 예시 텍스트(레이아웃 XML에서도 이미 설정되어 있지만, 혹시 변경 필요 시 코드로 세팅 가능)
        // tvTargetHint.setText("새연양: 저는 신설동에서 내려야 해요!");
        // tvBusLine.setText("서울역 → 시청 → 종로3가 → 동대문 → 신설동 → 청량리");
        // tvQuestion.setText("새연양은 몇 개 뒤에 내려야 할까요?");

        // 정답 위치 계산
        correctPosition = calculateCorrectPosition(
                tvTargetHint.getText().toString(),
                tvBusLine.getText().toString()
        );

        Log.d(TAG, "정답 위치: " + correctPosition + "번");

        // 버튼 클릭 리스너 설정
        View.OnClickListener choiceListener = v -> {
            if (gameCompleted) return;

            Button clicked = (Button) v;
            int chosenNumber = Integer.parseInt(clicked.getText().toString());

            Log.d(TAG, "사용자 선택: " + chosenNumber + "번");
            checkAnswer(chosenNumber);
        };

        btn1.setOnClickListener(choiceListener);
        btn2.setOnClickListener(choiceListener);
        btn3.setOnClickListener(choiceListener);
        btn4.setOnClickListener(choiceListener);
        btn5.setOnClickListener(choiceListener);
        btn6.setOnClickListener(choiceListener);
    }

    /**
     * tvTargetHint에 나오는 정류장 이름과 tvBusLine에 나열된 정류장 목록을 바탕으로
     * 몇 번째 정류장인지 계산하여 반환.
     *
     * @param hintText  "새연양: 저는 신설동에서 내려야 해요!" 같은 문장
     * @param lineText  "서울역 → 시청 → 종로3가 → 동대문 → 신설동 → 청량리"
     * @return 1 기반 인덱스 위치 (예: 신설동이 5번째라면 5), 찾지 못하면 -1
     */
    private int calculateCorrectPosition(String hintText, String lineText) {
        // hintText에서 "신설동" 부분만 가져옴
        // 예: "새연양: 저는 신설동에서 내려야 해요!" => "신설동"
        String targetStation = "";
        int start = hintText.indexOf("저는 ");
        int end   = hintText.indexOf("에서");
        if (start != -1 && end != -1 && end > start + 3) {
            targetStation = hintText.substring(start + 3, end);
        }

        Log.d(TAG, "목표 정류장: " + targetStation);

        // lineText를 "→" 기준으로 분할하여 배열로 저장
        String[] stops = lineText.split("→");
        for (int i = 0; i < stops.length; i++) {
            // 각 요소 앞뒤 공백 제거
            String stopName = stops[i].trim();
            if (stopName.equals(targetStation)) {
                // 🎯 1 기반 인덱스 반환 (버튼이 1~6번이므로)
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * 🎯 사용자가 선택한 숫자가 정답인지 확인하고 토스트 메시지로 피드백
     *
     * @param chosenNumber 사용자가 누른 버튼의 숫자 (1~6)
     */
    private void checkAnswer(int chosenNumber) {
        if (gameCompleted) return;

        gameCompleted = true;

        if (chosenNumber == correctPosition) {
            Toast.makeText(this, "정답입니다! 🎉", Toast.LENGTH_LONG).show();
            Log.d(TAG, "✅ 정답!");

            // 🎯 정답 시 결과 반환
            finishWithResult(true, 100);
        } else if (correctPosition == -1) {
            Toast.makeText(this, "정답을 계산할 수 없습니다.", Toast.LENGTH_LONG).show();
            mistakeCount = 1;
            Log.d(TAG, "❌ 정답 계산 실패");

            finishWithResult(false, 0);
        } else {
            Toast.makeText(this, "오답입니다… 정답은 " + correctPosition + "번입니다.", Toast.LENGTH_LONG).show();
            mistakeCount = 1;
            Log.d(TAG, "❌ 오답");

            // 🎯 정답 버튼 강조 표시
            highlightCorrectAnswer();

            // 🎯 오답 시 결과 반환 (50점)
            finishWithResult(false, 50);
        }

        // 정답 확인 후 버튼 비활성화
        disableAllButtons();
    }

    /**
     * 🎯 정답 버튼을 강조 표시
     */
    private void highlightCorrectAnswer() {
        Button[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6};
        for (int i = 0; i < buttons.length; i++) {
            if (Integer.parseInt(buttons[i].getText().toString()) == correctPosition) {
                buttons[i].setBackgroundResource(android.R.color.holo_green_light);
                break;
            }
        }
    }

    /**
     * 🎯 게임 결과를 Story8Activity로 반환
     */
    private void finishWithResult(boolean success, int score) {
        Log.d(TAG, "게임 완료 - 성공: " + success + ", 점수: " + score + ", 실수: " + mistakeCount);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("mistakes", mistakeCount);
        resultIntent.putExtra("success", success);

        setResult(success ? RESULT_OK : RESULT_CANCELED, resultIntent);

        // 2초 후 종료하여 사용자가 피드백을 볼 시간 확보
        findViewById(android.R.id.content).postDelayed(this::finish, 2000);
    }

    private void disableAllButtons() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
    }
}