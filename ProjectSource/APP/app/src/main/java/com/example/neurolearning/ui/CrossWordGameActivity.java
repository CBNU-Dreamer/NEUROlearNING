package com.example.neurolearning.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class CrossWordGameActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView tvNumber, tvHint;
    private EditText etAnswer;
    private Button btnCheck;

    private Square[][] board;
    private Word[] words;
    private int currentWordIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword_game);

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
                    board[r][c].view.setBackgroundColor(Color.YELLOW);  // 정답 입력된 칸은 하얀색 유지
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
        if (input.equals(w.answer)) {
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
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
                tvHint.setText("게임 완료! 축하합니다.");
                etAnswer.setEnabled(false);
                btnCheck.setEnabled(false);
            }
        } else {
            Toast.makeText(this, "오답입니다. 다시 시도해보세요!", Toast.LENGTH_SHORT).show();
        }
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
                new Word(4, 0, 5, true, "과자", "달콤한 간식"),
                new Word(3, 1, 2, false, "구급상자", "응급 처치 도구 상자"),
                new Word(5, 3, 4, false, "소화제", "속이 안 좋을 때 먹는 약"),
                new Word(7, 3, 4, true, "소시지", "육가공품"),
                new Word(6, 4, 3, false, "접시", "음식을 담는 그릇"),
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
