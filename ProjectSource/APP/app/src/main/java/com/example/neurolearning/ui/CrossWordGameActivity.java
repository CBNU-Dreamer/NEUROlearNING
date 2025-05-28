package com.example.neurolearning.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.GameProgressRepository;

public class CrossWordGameActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView tvNumber, tvHint;
    private EditText etAnswer;
    private Button btnCheck;

    private Square[][] board;
    private Word[] words;
    private int currentWordIndex = 0;

    // DB 관련 추가
    private GameProgressRepository gameProgressRepository;
    private String currentUsername;
    private int currentStoryNumber;
    private long gameStartTime;
    private int correctAnswers = 0; // 정답 개수
    private int totalAttempts = 0;  // 총 시도 횟수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword_game);

        // 사용자 정보 가져오기 (추가)
        currentUsername = getIntent().getStringExtra("username");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 1);
        if (currentUsername == null) {
            currentUsername = "testuser";
        }

        // Repository 초기화 및 게임 시작 시간 기록 (추가)
        gameProgressRepository = new GameProgressRepository(getApplication());
        gameStartTime = System.currentTimeMillis();

        gridLayout = findViewById(R.id.gridLayout);
        tvNumber = findViewById(R.id.tvNumber);
        tvHint = findViewById(R.id.tvHint);
        etAnswer = findViewById(R.id.etAnswer);
        btnCheck = findViewById(R.id.btnCheck);

        words = getWords();
        board = new Square[8][8];

        drawGrid();          // 그리드 생성
        showWordSet();       // 단어가 있는 셀만 흰색
        showCurrentWord();   // 현재 문제 강조 (초록색)

        btnCheck.setOnClickListener(v -> checkAnswer());
    }

    private void drawGrid() {
        gridLayout.removeAllViews();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                TextView cell = new TextView(this);
                cell.setWidth(130);
                cell.setHeight(130);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(24);
                cell.setBackgroundColor(Color.parseColor("#BBDEFB"));
                Square sq = new Square(cell, r, c);
                board[r][c] = sq;
                cell.setOnClickListener(v -> handleCellClick(sq.row, sq.col));
                gridLayout.addView(cell);
            }
        }
    }

    private void showWordSet() {
        for (Word word : words) {
            for (int i = 0; i < word.answer.length(); i++) {
                int r = word.startRow + (word.vertical ? i : 0);
                int c = word.startCol + (word.vertical ? 0 : i);
                board[r][c].view.setBackgroundColor(Color.WHITE); // 기본 하얀색
            }
        }
    }

    private void showCurrentWord() {
        Word w = words[currentWordIndex];
        tvNumber.setText(String.valueOf(w.number));
        tvHint.setText(w.hint);
        etAnswer.setText("");

        // reset
        for (Word word : words) {
            for (int i = 0; i < word.answer.length(); i++) {
                int r = word.startRow + (word.vertical ? i : 0);
                int c = word.startCol + (word.vertical ? 0 : i);

                if (!word.filled) {
                    showWordSet();
                    board[r][c].view.setBackgroundColor(Color.parseColor("#BBDEFB"));  // 초기 배경으로 복원
                } else {
                    showWordSet();
                    board[r][c].view.setBackgroundColor(Color.YELLOW);  // 정답 입력된 칸은 노란색 유지
                    board[r][c].view.setText(String.valueOf(word.answer.charAt(i)));
                }
            }
        }

        for (int i = 0; i < w.answer.length(); i++) {
            int r = w.startRow + (w.vertical ? i : 0);
            int c = w.startCol + (w.vertical ? 0 : i);
            board[r][c].view.setBackgroundColor(Color.parseColor("#C8E6C9"));
        }
    }

    private void checkAnswer() {
        String input = etAnswer.getText().toString().trim();
        Word w = words[currentWordIndex];
        totalAttempts++; // 시도 횟수 증가 (추가)

        if (input.equals(w.answer)) {
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            correctAnswers++; // 정답 개수 증가 (추가)
            w.filled = true;

            for (int i = 0; i < w.answer.length(); i++) {
                int r = w.startRow + (w.vertical ? i : 0);
                int c = w.startCol + (w.vertical ? 0 : i);
                board[r][c].view.setText(String.valueOf(w.answer.charAt(i)));
                board[r][c].view.setBackgroundColor(Color.YELLOW);
            }

            currentWordIndex++;
            if (currentWordIndex < words.length) {
                showCurrentWord();
            } else {
                // 게임 완료 처리 (수정)
                completeGame();
            }
        } else {
            Toast.makeText(this, "오답입니다. 다시 시도해보세요!", Toast.LENGTH_SHORT).show();
        }
    }

    // 게임 완료 처리 메서드 (추가)
    private void completeGame() {
        // UI 비활성화
        tvHint.setText("게임 완료! 축하합니다.");
        etAnswer.setEnabled(false);
        btnCheck.setEnabled(false);

        // 게임 결과 계산
        long completionTime = System.currentTimeMillis() - gameStartTime;
        int score = calculateScore(); // 점수 계산
        int mistakes = totalAttempts - correctAnswers; // 실수 횟수

        // DB에 게임 기록 저장
        gameProgressRepository.saveGamePlayRecord(
                currentUsername,
                currentStoryNumber,
                "CROSSWORD", // 게임 타입
                score,
                true, // 성공
                mistakes,
                completionTime / 1000 // 초 단위로 변환
        );

        // 잠시 후 완료 메시지와 함께 Story1Activity로 이동
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "수고하셨습니다!", Toast.LENGTH_LONG).show();

            // Story1Activity로 결과 전달하며 이동
            Intent resultIntent = new Intent();
            resultIntent.putExtra("score", score);
            resultIntent.putExtra("completionTime", completionTime);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 1500); // 1.5초 후 실행
    }

    // 점수 계산 메서드 (추가)
    private int calculateScore() {
        // 기본 점수 100점에서 실수할 때마다 -5점
        int baseScore = 100;
        int mistakes = totalAttempts - correctAnswers;
        int finalScore = Math.max(baseScore - (mistakes * 5), 10); // 최소 10점 보장
        return finalScore;
    }

    private void handleCellClick(int row, int col) {
        showWordSet();
        for (int i = 0; i < words.length; i++) {
            Word w = words[i];
            for (int j = 0; j < w.answer.length(); j++) {
                int r = w.startRow + (w.vertical ? j : 0);
                int c = w.startCol + (w.vertical ? 0 : j);
                if (r == row && c == col) {
                    currentWordIndex = i;
                    showCurrentWord();
                    return;
                }
            }
        }
    }

    private Word[] getWords() {
        return new Word[]{
                new Word(1, 0, 1, false, "연고", "피부에 바르는 약"),
                new Word(2, 0, 2, true, "고구마", "달콤한 구황작물"),
                new Word(3, 1, 2, false, "구급상자", "응급 처치 도구 상자"),
                new Word(4, 0, 5, true, "과자", "달콤한 간식"),
                new Word(5, 3, 4, false, "소화제", "속이 안 좋을 때 먹는 약"),
                new Word(6, 4, 3, false, "접시", "음식을 담는 그릇"),
                new Word(7, 3, 4, true, "소시지", "육가공품"),
                new Word(8, 5, 4, false, "지우개", "연필 흔적 지우는 것"),
                new Word(9, 5, 5, true, "우산", "비 올 때 쓰는 소형 장막")
        };
    }

    static class Square {
        TextView view;
        int row, col;
        Square(TextView v, int r, int c) {
            view = v;
            row = r;
            col = c;
        }
    }

    static class Word {
        int number;
        int startRow, startCol;
        boolean vertical;
        String answer;
        String hint;
        boolean filled = false;

        Word(int number, int r, int c, boolean v, String a, String h) {
            this.number = number;
            startRow = r;
            startCol = c;
            vertical = v;
            answer = a;
            hint = h;
        }
    }
}