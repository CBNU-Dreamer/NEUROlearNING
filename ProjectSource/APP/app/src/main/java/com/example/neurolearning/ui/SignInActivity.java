package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    private UserRepository userRepository;

    private EditText editTextName;
    private EditText editTextPhone;
    private Button btnLogin;
    private TextView txtFindPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Repository 초기화
        userRepository = new UserRepository(getApplication());

        // UI 요소 연결
        initViews();

        // 이벤트 리스너 설정
        setClickListeners();

        // 뒤로가기 콜백 설정
        setupBackPressedCallback();
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        btnLogin = findViewById(R.id.btnLogin);
        txtFindPw = findViewById(R.id.txtFindPw);
    }

    private void setClickListeners() {
        // 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(v -> performLogin());

        // 회원가입 텍스트 클릭 이벤트
        txtFindPw.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performLogin() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // 입력값 유효성 검사
        if (name.isEmpty()) {
            editTextName.setError("이름을 입력해주세요.");
            editTextName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("연락처를 입력해주세요.");
            editTextPhone.requestFocus();
            return;
        }

        // 🎯 새로운 로그인 방식 (이름 + 전화번호)
        new Thread(() -> {
            try {
                Log.d(TAG, "로그인 시도: " + name + ", " + phone);
                User user = userRepository.loginUser(name, phone);

                runOnUiThread(() -> {
                    if (user != null) {
                        // 로그인 성공
                        Log.d(TAG, "✅ 로그인 성공: " + user.getName() + " (UUID: " + user.getUserId() + ")");
                        Toast.makeText(this, user.getName() + "님, 환영합니다!", Toast.LENGTH_SHORT).show();

                        // StoryActivity로 이동 (사용자 ID 전달)
                        Intent intent = new Intent(SignInActivity.this, StoryActivity.class);
                        intent.putExtra("userId", user.getUserId()); // UUID 전달
                        intent.putExtra("userName", user.getName());  // 이름도 전달
                        startActivity(intent);
                        finish();
                    } else {
                        // 로그인 실패
                        Log.d(TAG, "❌ 로그인 실패: 사용자를 찾을 수 없음");
                        Toast.makeText(this, "등록되지 않은 사용자입니다.\n이름과 연락처를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                        editTextName.setError("등록되지 않은 사용자입니다");
                        editTextPhone.setError("등록되지 않은 사용자입니다");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ 로그인 처리 중 오류", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // 뒤로가기 버튼 처리
    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 회원가입 화면으로 돌아가기
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}