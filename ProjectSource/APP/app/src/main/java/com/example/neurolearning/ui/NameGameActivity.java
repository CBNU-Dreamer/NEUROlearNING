package com.example.neurolearning.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

import java.util.Random;

public class NameGameActivity extends AppCompatActivity {
    private static final String TAG = "NameGameActivity";

    // 이웃 이름 목록 및 대응 이미지 리소스
    private final String[] neighborNames = {
            "정새연", "카구야", "배수연", "야마다", "에렌", "배새연"
    };
    private final int[] neighborDrawables = {
            R.drawable.neigh1,
            R.drawable.neigh2,
            R.drawable.neigh3,
            R.drawable.neigh4,
            R.drawable.neigh5,
            R.drawable.neigh6
    };

    // 레이아웃1 요소
    private GridLayout gridNeighbors;
    private Button btnConfirm;

    // 레이아웃2 요소
    private ImageView ivTargetNeighbor;
    private Button btnAnswer;

    // 게임 상태
    private int targetIndex;  // 정답 이웃 인덱스 (0~5)
    private boolean isPhaseOne = true;

    // 🎯 새로운 DB 구조를 위한 필드들
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private int mistakeCount = 0;
    private boolean gameCompleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 7);

        Log.d(TAG, "이름 맞추기 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        // 초기에는 layout1 (activity_name1_game.xml) 사용
        setContentView(R.layout.activity_name1_game);
        setupPhaseOne();
    }

    /**
     * Phase 1: 6명의 이웃과 이름을 보여주고 확인 버튼 누르면 Phase 2로 전환
     */
    private void setupPhaseOne() {
        isPhaseOne = true;
        gridNeighbors = findViewById(R.id.gridChoices);
        btnConfirm = findViewById(R.id.btnConfirm);

        Log.d(TAG, "Phase 1: 이웃 소개 화면");

        // GridLayout 내부의 6개의 텍스트뷰/이미지는 XML에서 이미 정의되어 있으므로
        // 추가 작업은 필요하지 않습니다. 그냥 Confirm 버튼 클릭 시 Phase 2로.
        btnConfirm.setOnClickListener(v -> {
            if (!gameCompleted) {
                // Phase 2 시작
                startPhaseTwo();
            }
        });
    }

    /**
     * Phase 2: 랜덤으로 이웃 한 명을 보여주고, 사용자가 이름을 맞추도록 다이얼로그 표시
     */
    private void startPhaseTwo() {
        isPhaseOne = false;
        Log.d(TAG, "Phase 2: 이름 맞추기 시작");

        // layout2 로 전환
        setContentView(R.layout.activity_name2_game);

        ivTargetNeighbor = findViewById(R.id.ivTargetNeighbor);
        btnAnswer = findViewById(R.id.btnAnswer);

        // 정답 인덱스를 랜덤으로 선택
        targetIndex = new Random().nextInt(neighborNames.length);
        String correctName = neighborNames[targetIndex];

        Log.d(TAG, "정답: " + correctName + " (인덱스: " + targetIndex + ")");

        // 해당 이웃 이미지 설정
        ivTargetNeighbor.setImageResource(neighborDrawables[targetIndex]);

        btnAnswer.setOnClickListener(v -> {
            if (!gameCompleted) {
                showAnswerDialog();
            }
        });
    }

    /**
     * 이름 선택 다이얼로그를 띄움
     */
    private void showAnswerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이웃의 이름을 선택하세요");

        builder.setItems(neighborNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkAnswer(which);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 🎯 사용자가 선택한 인덱스를 정답과 비교 후 피드백
     */
    private void checkAnswer(int chosenIndex) {
        if (gameCompleted) return;

        gameCompleted = true;
        String chosenName = neighborNames[chosenIndex];
        String correctName = neighborNames[targetIndex];

        Log.d(TAG, "사용자 선택: " + chosenName + ", 정답: " + correctName);

        if (chosenIndex == targetIndex) {
            Toast.makeText(this, "정답입니다! 🎉", Toast.LENGTH_LONG).show();
            Log.d(TAG, "✅ 정답!");

            // 🎯 정답 시 결과 반환
            finishWithResult(true, 100);
        } else {
            Toast.makeText(this, "오답입니다… 정답은 " + correctName + " 입니다.", Toast.LENGTH_LONG).show();
            mistakeCount = 1;
            Log.d(TAG, "❌ 오답");

            // 🎯 오답 시 결과 반환 (50점)
            finishWithResult(false, 50);
        }
    }

    /**
     * 🎯 게임 결과를 Story7Activity로 반환
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

    @Override
    public void onBackPressed() {
        // 뒤로가기 시 Phase1에서는 바로 종료,
        // Phase2에서는 Phase1으로 돌아가기를 원한다면 아래처럼 수정 가능
        if (isPhaseOne) {
            super.onBackPressed();
        } else {
            // Phase2에서 뒤로 누르면 Phase1 화면으로 복귀
            Log.d(TAG, "뒤로가기: Phase1으로 복귀");
            setContentView(R.layout.activity_name1_game);
            setupPhaseOne();
            gameCompleted = false; // 게임 상태 초기화
            mistakeCount = 0;
        }
    }
}