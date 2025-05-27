package com.example.neurolearning.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.neurolearning.R;

import java.util.Random;

public class RspGameActivity extends AppCompatActivity {
    private FrameLayout contentFrame;
    private TextView tvTitle, tvSubtitle;
    private String npcChoice;
    private String[] choices = {"가위", "바위", "보"};
    private String playerCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_game); // 재사용

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        contentFrame = findViewById(R.id.contentFrame);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        startGame();
    }

    private int getImageResId(String choice) {
        switch (choice) {
            case "가위":
                return R.drawable.ic_rsp_scissors;
            case "바위":
                return R.drawable.ic_rsp_rock;
            case "보":
                return R.drawable.ic_rsp_paper;
            default:
                return 0;
        }
    }

    private int dp(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }


    private void startGame() {
        tvTitle.setText("가위바위보 게임");
        tvSubtitle.setText("조건에 맞게 가위, 바위, 보 중 하나를 내세요!");

        npcChoice = getRandomChoice();
        playerCondition = getRandomCondition();

        contentFrame.removeAllViews();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(16, 16, 16, 16);

        // NPC 대화 + 이미지
        TextView tvNpc = new TextView(this);
        tvNpc.setText("예술양: 저는 " + npcChoice + "를 냈어요!");
        tvNpc.setTextSize(18f);
        layout.addView(tvNpc);

        ImageView npcImg = new ImageView(this);
        npcImg.setImageResource(getImageResId(npcChoice));
        npcImg.setLayoutParams(new LinearLayout.LayoutParams(dp(120), dp(120)));
        layout.addView(npcImg);

        // 조건 텍스트
        TextView tvCond = new TextView(this);
        tvCond.setText(playerCondition);
        tvCond.setTextSize(20f);
        tvCond.setTextColor(Color.BLUE);
        tvCond.setPadding(0, dp(16), 0, dp(16));
        layout.addView(tvCond);

        // 이미지 버튼 (가위/바위/보)
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setGravity(Gravity.CENTER);

        for (String choice : choices) {
            ImageButton btn = new ImageButton(this);
            btn.setImageResource(getImageResId(choice));
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(100), dp(100));
            lp.setMargins(dp(8), dp(8), dp(8), dp(8));
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> checkResult(choice));
            btnLayout.addView(btn);
        }

        layout.addView(btnLayout);
        contentFrame.addView(layout);
    }


    private String getRandomChoice() {
        return choices[new Random().nextInt(3)];
    }

    private String getRandomCondition() {
        String[] conditions = {
                "이기지도 말고, 비기지도 마세요!",
                "무조건 이기세요!",
                "무조건 지세요!",
                "지지도 말고, 비기지도 마세요!"
        };
        return conditions[new Random().nextInt(conditions.length)];
    }

    private void checkResult(String playerChoice) {
        String result = getResult(playerChoice, npcChoice);
        boolean success = false;

        if (playerCondition.equals("무조건 이기세요!")) {
            success = result.equals("win");
        } else if (playerCondition.equals("무조건 지세요!")) {
            success = result.equals("lose");
        } else if (playerCondition.equals("이기지도 말고, 비기지도 마세요!")) {
            success = result.equals("lose");
        } else if (playerCondition.equals("지지도 말고, 비기지도 마세요!")) {
            success = result.equals("win");
        }

        if (success) {
            Toast.makeText(this, "성공! 조건을 만족했어요!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "실패! 조건을 만족하지 못했어요.\n다시 도전해보세요!", Toast.LENGTH_SHORT).show();
            startGame(); // 다시 시작
        }
    }


    private String getResult(String player, String npc) {
        if (player.equals(npc)) return "draw";
        if ((player.equals("가위") && npc.equals("보")) ||
                (player.equals("바위") && npc.equals("가위")) ||
                (player.equals("보") && npc.equals("바위"))) {
            return "win";
        } else {
            return "lose";
        }
    }
}
