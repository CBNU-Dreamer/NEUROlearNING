// File: BusStopGameActivity.java
package com.example.neurolearning.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class BusStopGameActivity extends AppCompatActivity {

    private TextView tvTargetHint;
    private TextView tvBusLine;
    private TextView tvQuestion;

    private Button btn1, btn2, btn3, btn4, btn5, btn6;

    // 정답 정류장 인덱스 (0부터 카운트)
    private int correctPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busstop_game);

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

        // 버튼 클릭 리스너 설정
        View.OnClickListener choiceListener = v -> {
            Button clicked = (Button) v;
            int chosenNumber = Integer.parseInt(clicked.getText().toString());
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
     * @return 0 기반 인덱스 위치 (예: 신설동이 네 번째 인덱스라면 4), 찾지 못하면 -1
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

        // lineText를 "→" 기준으로 분할하여 배열로 저장
        String[] stops = lineText.split("→");
        for (int i = 0; i < stops.length; i++) {
            // 각 요소 앞뒤 공백 제거
            String stopName = stops[i].trim();
            if (stopName.equals(targetStation)) {
                // 인덱스 i가 0부터 시작
                return i;
            }
        }
        return -1;
    }

    /**
     * 사용자가 선택한 숫자가 정답인지 확인하고 토스트 메시지로 피드백
     *
     * @param chosenNumber 사용자가 누른 버튼의 숫자 (1~6)
     */
    private void checkAnswer(int chosenNumber) {
        if (chosenNumber == correctPosition) {
            Toast.makeText(this, "정답입니다! 🎉", Toast.LENGTH_LONG).show();
        } else if (correctPosition == -1) {
            Toast.makeText(this, "정답을 계산할 수 없습니다.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "오답입니다… 정답은 " + correctPosition + "번입니다.", Toast.LENGTH_LONG).show();
        }
        // 정답 확인 후 버튼 비활성화
        disableAllButtons();
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
