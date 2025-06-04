package com.example.neurolearning.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class LetterGameActivity extends AppCompatActivity {

    private Button btnMsg1, btnMsg2, btnMsg3, btnMsg4, btnMsg5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_game);

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

        // 3) 각 버튼 클릭 시 토스트로 선택된 메시지 출력
        btnMsg1.setOnClickListener(v -> showToastWithText(btnMsg1.getText().toString()));
        btnMsg2.setOnClickListener(v -> showToastWithText(btnMsg2.getText().toString()));
        btnMsg3.setOnClickListener(v -> showToastWithText(btnMsg3.getText().toString()));
        btnMsg4.setOnClickListener(v -> showToastWithText(btnMsg4.getText().toString()));
        btnMsg5.setOnClickListener(v -> showToastWithText(btnMsg5.getText().toString()));
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
     * 선택한 버튼의 텍스트를 토스트로 보여줍니다.
     */
    private void showToastWithText(String message) {
        Toast.makeText(this, "선택: " + message, Toast.LENGTH_SHORT).show();
    }
}
