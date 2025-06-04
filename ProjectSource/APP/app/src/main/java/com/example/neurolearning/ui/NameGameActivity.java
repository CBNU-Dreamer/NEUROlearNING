// File: NameGameActivity.java
package com.example.neurolearning.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

import java.util.Random;

public class NameGameActivity extends AppCompatActivity {

    // 이웃 이름 목록 및 대응 이미지 리소스
    private final String[] neighborNames = {
            "정새연", "카구야", "배수연", "야마다", "에렌", "배새연"
    };
    private final int[] neighborDrawables = {
            R.drawable.neighbor1,
            R.drawable.neighbor2,
            R.drawable.neighbor3,
            R.drawable.neighbor4,
            R.drawable.neighbor5,
            R.drawable.neighbor6
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // GridLayout 내부의 6개의 텍스트뷰/이미지는 XML에서 이미 정의되어 있으므로
        // 추가 작업은 필요하지 않습니다. 그냥 Confirm 버튼 클릭 시 Phase 2로.
        btnConfirm.setOnClickListener(v -> {
            // Phase 2 시작
            startPhaseTwo();
        });
    }

    /**
     * Phase 2: 랜덤으로 이웃 한 명을 보여주고, 사용자가 이름을 맞추도록 다이얼로그 표시
     */
    private void startPhaseTwo() {
        isPhaseOne = false;
        // layout2 로 전환
        setContentView(R.layout.activity_name2_game);

        ivTargetNeighbor = findViewById(R.id.ivTargetNeighbor);
        btnAnswer = findViewById(R.id.btnAnswer);

        // 정답 인덱스를 랜덤으로 선택
        targetIndex = new Random().nextInt(neighborNames.length);
        // 해당 이웃 이미지 설정
        ivTargetNeighbor.setImageResource(neighborDrawables[targetIndex]);

        btnAnswer.setOnClickListener(v -> showAnswerDialog());
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
     * 사용자가 선택한 인덱스를 정답과 비교 후 피드백
     */
    private void checkAnswer(int chosenIndex) {
        if (chosenIndex == targetIndex) {
            Toast.makeText(this, "정답입니다! 🎉", Toast.LENGTH_LONG).show();
        } else {
            String correctName = neighborNames[targetIndex];
            Toast.makeText(this, "오답입니다… 정답은 " + correctName + " 입니다.", Toast.LENGTH_LONG).show();
        }
        // 게임 종료: 이 액티비티 finish
        finish();
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 시 Phase1에서는 바로 종료,
        // Phase2에서는 Phase1으로 돌아가기를 원한다면 아래처럼 수정 가능
        if (isPhaseOne) {
            super.onBackPressed();
        } else {
            // Phase2에서 뒤로 누르면 Phase1 화면으로 복귀
            setContentView(R.layout.activity_name1_game);
            setupPhaseOne();
        }
    }
}
