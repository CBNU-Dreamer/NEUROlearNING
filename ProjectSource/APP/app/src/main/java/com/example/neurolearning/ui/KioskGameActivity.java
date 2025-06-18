package com.example.neurolearning.ui;

import android.content.Intent;
import android.util.TypedValue;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.neurolearning.R;
import java.util.ArrayList;
import java.util.List;

public class KioskGameActivity extends AppCompatActivity {
    private static final String TAG = "KioskGame";

    private TextView tvTitle, tvSubtitle;
    private FrameLayout contentFrame;
    private final List<CartItem> cart = new ArrayList<>();

    // 🎯 새로운 DB 구조에 맞는 사용자 정보
    private String currentUserId;
    private String currentUserName;
    private int currentStoryNumber;
    private long gameStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_game);

        // 🎯 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");
        currentStoryNumber = getIntent().getIntExtra("storyNumber", 2);

        if (currentUserId == null) {
            Log.e(TAG, "❌ 사용자 ID가 null입니다");
            currentUserId = "unknown";
            currentUserName = "Unknown User";
        }

        Log.d(TAG, "키오스크 게임 시작: " + currentUserName + " (Story " + currentStoryNumber + ")");

        // 게임 시작 시간 기록
        gameStartTime = System.currentTimeMillis();

        initViews();
        showStep1();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        contentFrame = findViewById(R.id.contentFrame);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void showStep1() {
        tvTitle.setText("키오스크를 사용해봐요!");
        tvSubtitle.setText("1. 화면을 클릭하세요.");
        tvSubtitle.setTextSize(30);
        contentFrame.removeAllViews();

        View v = new View(this);
        v.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        v.setBackgroundColor(Color.LTGRAY);
        v.setOnClickListener(x -> showStep2());
        contentFrame.addView(v);
    }

    private void showStep2() {
        tvSubtitle.setText("2. 먹고가기를 선택하세요!");
        tvSubtitle.setTextSize(30);
        contentFrame.removeAllViews();

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setGravity(Gravity.CENTER);
        lay.setBackgroundColor(Color.LTGRAY);

        Button btnPack = createBtn("포장");
        btnPack.setTextSize(30);
        btnPack.setOnClickListener(x ->
                Toast.makeText(this, "다시 선택해주세요!", Toast.LENGTH_SHORT).show()
        );

        Button btnDine = createBtn("먹고가기");
        btnDine.setTextSize(30);
        btnDine.setOnClickListener(x -> showStep3());

        lay.addView(btnPack);
        lay.addView(btnDine);
        contentFrame.addView(lay);
    }

    private void showStep3() {
        tvSubtitle.setText("3. 버거를 선택하세요!");
        tvSubtitle.setTextSize(30);
        contentFrame.removeAllViews();

        // 키오스크 UI 구성
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 헤더
        TextView header = new TextView(this);
        header.setText("메뉴");
        header.setTextColor(Color.WHITE);
        header.setTextSize(18f);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(Color.parseColor("#feead2"));
        parent.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(48)
        ));

        // 본문 영역
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ));
        parent.addView(body);

        // 네비게이션
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.VERTICAL);
        nav.setBackgroundColor(Color.parseColor("#feead2"));
        body.addView(nav, new LinearLayout.LayoutParams(dp(48), ViewGroup.LayoutParams.MATCH_PARENT));

        String[] tabs = {"단일","세트","이벤트"};
        for (String t: tabs) {
            TextView tv = new TextView(this);
            tv.setText(t);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            nav.addView(tv, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ));
        }

        // 컨텐츠 영역
        FrameLayout content = new FrameLayout(this);
        content.setBackgroundColor(Color.LTGRAY);
        body.addView(content, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
        ));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        glp.setMargins(dp(16), dp(16), dp(16), dp(16));
        content.addView(grid, glp);

        String[] cats = {"버거","세트","사이드","음료&커피"};
        for (String c: cats) {
            Button btn = new Button(this);
            btn.setText(c);
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(18);
            btn.setBackgroundColor(Color.parseColor("#feead2"));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(140);
            lp.height = dp(80);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(8), dp(8), dp(8), dp(8));
            grid.addView(btn, lp);

            btn.setOnClickListener(v -> {
                if (c.equals("버거")) {
                    showStep4();
                } else {
                    Toast.makeText(this, "다시 선택해주세요!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        contentFrame.addView(parent);
    }

    private void showStep4() {
        tvSubtitle.setText("4. 불고기 버거를 드셔보실래요?");
        tvSubtitle.setTextSize(30);
        contentFrame.removeAllViews();

        // 버거 선택 UI (showStep3과 유사한 구조)
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 헤더
        TextView header = new TextView(this);
        header.setText("버거");
        header.setTextColor(Color.WHITE);
        header.setTextSize(18f);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(Color.parseColor("#feead2"));
        parent.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(48)
        ));

        // 본문 영역
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ));
        parent.addView(body);

        // 네비게이션
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.VERTICAL);
        nav.setBackgroundColor(Color.parseColor("#feead2"));
        body.addView(nav, new LinearLayout.LayoutParams(dp(48), ViewGroup.LayoutParams.MATCH_PARENT));

        String[] tabs = {"단일", "세트", "이벤트"};
        for (String t : tabs) {
            TextView tv = new TextView(this);
            tv.setText(t);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            nav.addView(tv, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ));
        }

        // 컨텐츠 영역
        FrameLayout content = new FrameLayout(this);
        content.setBackgroundColor(Color.LTGRAY);
        body.addView(content, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
        ));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        glp.setMargins(dp(16), dp(16), dp(16), dp(16));
        content.addView(grid, glp);

        // 버거 버튼들
        String[] burgers = {"불고기 버거", "치킨 버거", "치즈 버거"};
        for (String b : burgers) {
            Button btn = new Button(this);
            btn.setText(b);
            btn.setTextSize(18);
            btn.setTextColor(Color.WHITE);
            btn.setBackgroundColor(Color.parseColor("#feead2"));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 140;
            lp.height = dp(80);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(8), dp(8), dp(8), dp(8));
            grid.addView(btn, lp);

            btn.setOnClickListener(v -> {
                if (b.equals("불고기 버거")) {
                    cart.add(new CartItem(b, 4000));
                    showStep5();
                } else {
                    Toast.makeText(this, "다시 선택해주세요!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        contentFrame.addView(parent);
    }

    // TextView 헬퍼를 이렇게 바꿔두고…
    private TextView createCell(String txt, float textSizeSp) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        // SP 단위로 크기 지정
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        tv.setPadding(dp(8), dp(8), dp(8), dp(8));
        return tv;
    }


    private void showStep5() {

        tvSubtitle.setText("5. 장바구니에 성공적으로 담겼습니다. 결제를 눌러주세요!");
        tvSubtitle.setTextSize(30);
        contentFrame.removeAllViews();

        TableLayout table = new TableLayout(this);
        table.setStretchAllColumns(true);
        table.setBackgroundColor(Color.LTGRAY);


        TableRow hdr = new TableRow(this);
        hdr.addView(createCell("메뉴", 30));
        hdr.addView(createCell("수량", 30));
        hdr.addView(createCell("가격", 30));
        table.addView(hdr);

        for (CartItem it : cart) {
            TableRow row = new TableRow(this);
            row.addView(createCell(it.name,     30));
            row.addView(createCell("1",          30));
            row.addView(createCell(it.price + "원", 30));
            table.addView(row);
        }

        Button pay = createBtn("결제");
        pay.setTextSize(30);
        pay.setOnClickListener(x -> showStep6());

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(table);
        lay.addView(pay);
        contentFrame.addView(lay);
    }

    private void showStep6() {
        tvSubtitle.setText("6. 결제가 성공적으로 완료되었습니다!");
        tvSubtitle.setTextSize(30);

        // 🎯 게임 완료 처리
        long completionTime = System.currentTimeMillis() - gameStartTime;
        int score = 100; // 키오스크 게임은 완료하면 100점

        Log.d(TAG, "✅ 키오스크 게임 완료");
        Log.d(TAG, "점수: " + score + ", 시간: " + (completionTime/1000) + "초");

        // Story2Activity로 결과 전달
        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("completionTime", completionTime);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // dp 변환 헬퍼
    private int dp(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private Button createBtn(String txt) {
        Button btn = new Button(this);
        btn.setText(txt);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        p.setMargins(16, 16, 16, 16);
        btn.setLayoutParams(p);
        return btn;
    }

    private TextView createCell(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private static class CartItem {
        String name;
        int price;
        CartItem(String n, int p) {
            name = n;
            price = p;
        }
    }
}