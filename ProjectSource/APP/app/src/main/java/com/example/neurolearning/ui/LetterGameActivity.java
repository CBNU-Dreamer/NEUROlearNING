package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class LetterGameActivity extends AppCompatActivity {
    private static final String TAG = "LetterGameActivity";

    private Button btnMsg1, btnMsg2, btnMsg3, btnMsg4, btnMsg5;

    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int selectionCount = 0; // 선택한 메시지 개수
    private boolean gameCompleted = false;

    // 🎯 정답 메시지 인덱스 (예: 3번째 메시지가 정답이라면 2)
    private final int correctMessageIndex = 2; // 0-based 인덱스

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_game);

        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 9);

        Log.d(TAG, "편지 선택 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        // 1) 액션바 뒤로가기 화살표 및 타이틀 설정
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("사랑하는 손녀");      // 액션바 중앙 타이틀
            ab.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        // 2) 버튼 바인딩
        btnMsg1 = findViewById(R.id.btn_msg1);
        btnMsg2 = findViewById(R.id.btn_msg2);
        btnMsg3 = findViewById(R.id.btn_msg3);
        btnMsg4 = findViewById(R.id.btn_msg4);
        btnMsg5 = findViewById(R.id.btn_msg5);

        // 3) 각 버튼 클릭 시 메시지 선택 처리
        btnMsg1.setOnClickListener(v -> handleMessageSelection(0, btnMsg1));
        btnMsg2.setOnClickListener(v -> handleMessageSelection(1, btnMsg2));
        btnMsg3.setOnClickListener(v -> handleMessageSelection(2, btnMsg3));
        btnMsg4.setOnClickListener(v -> handleMessageSelection(3, btnMsg4));
        btnMsg5.setOnClickListener(v -> handleMessageSelection(4, btnMsg5));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 액션바 뒤로가기 화살표 클릭 처리
        if (item.getItemId() == android.R.id.home) {
            finish();  // 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 🎯 메시지 선택 처리
     */
    private void handleMessageSelection(int selectedIndex, Button selectedButton) {
        if (gameCompleted) return;

        selectionCount++;
        String selectedMessage = selectedButton.getText().toString();

        Log.d(TAG, "메시지 선택: " + selectedIndex + "번 - " + selectedMessage);

        // 선택된 버튼 색상 변경 (시각적 피드백)
        selectedButton.setBackgroundResource(android.R.color.holo_blue_light);

        // 토스트로 선택 확인
        showToastWithText(selectedMessage);

        // 정답 확인
        if (selectedIndex == correctMessageIndex) {
            // 🎯 정답 선택
            gameCompleted = true;
            Toast.makeText(this, "완벽한 선택입니다! 💝", Toast.LENGTH_LONG).show();
            selectedButton.setBackgroundResource(android.R.color.holo_green_light);

            Log.d(TAG, "✅ 정답 선택!");
            finishWithResult(true, 100);

        } else {
            // 🎯 오답 선택 - 다시 선택 가능
            selectedButton.setBackgroundResource(android.R.color.holo_red_light);
            Toast.makeText(this, "다른 메시지도 생각해보세요...", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "❌ 오답, 재선택 가능");

            // 3초 후 버튼 색상 복원하여 다시 선택 가능하게 함
            selectedButton.postDelayed(() -> {
                if (!gameCompleted) {
                    selectedButton.setBackgroundResource(android.R.drawable.btn_default);
                }
            }, 3000);

            // 🎯 너무 많이 틀렸을 때 힌트 제공
            if (selectionCount >= 3) {
                Toast.makeText(this, "힌트: 따뜻하고 감사한 마음이 담긴 메시지를 찾아보세요!", Toast.LENGTH_LONG).show();
            }

            // 🎯 5번 이상 틀렸을 때 게임 종료
            if (selectionCount >= 5) {
                gameCompleted = true;
                highlightCorrectAnswer();
                finishWithResult(false, 50);
            }
        }
    }

    /**
     * 🎯 정답 메시지 강조 표시
     */
    private void highlightCorrectAnswer() {
        Button[] buttons = {btnMsg1, btnMsg2, btnMsg3, btnMsg4, btnMsg5};
        buttons[correctMessageIndex].setBackgroundResource(android.R.color.holo_green_light);
        Toast.makeText(this, "정답은 이 메시지였습니다!", Toast.LENGTH_LONG).show();
    }

    /**
     * 🎯 게임 결과를 Story9Activity로 반환
     */
    private void finishWithResult(boolean success, int score) {
        // 실수 횟수 = 총 선택 횟수 - 1 (정답 1번 제외)
        int mistakes = Math.max(0, selectionCount - 1);

        Log.d(TAG, "게임 완료 - 성공: " + success + ", 점수: " + score + ", 실수: " + mistakes);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("mistakes", mistakes);
        resultIntent.putExtra("success", success);

        setResult(success ? RESULT_OK : RESULT_CANCELED, resultIntent);

        // 모든 버튼 비활성화
        disableAllButtons();

        // 3초 후 종료하여 사용자가 피드백을 볼 시간 확보
        findViewById(android.R.id.content).postDelayed(this::finish, 3000);
    }

    /**
     * 선택한 버튼의 텍스트를 토스트로 보여줍니다.
     */
    private void showToastWithText(String message) {
        Toast.makeText(this, "선택: " + message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 🎯 모든 버튼 비활성화
     */
    private void disableAllButtons() {
        btnMsg1.setEnabled(false);
        btnMsg2.setEnabled(false);
        btnMsg3.setEnabled(false);
        btnMsg4.setEnabled(false);
        btnMsg5.setEnabled(false);
    }
}