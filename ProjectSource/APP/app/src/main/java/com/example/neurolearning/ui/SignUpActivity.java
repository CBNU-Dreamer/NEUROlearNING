package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;
import com.example.neurolearning.data.GameRecordRepository;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private UserRepository userRepository;
    private GameRecordRepository gameRecordRepository;

    private EditText editTextName;
    private EditText editTextUserPhone;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Repository 초기화
        userRepository = new UserRepository(getApplication());
        gameRecordRepository = new GameRecordRepository(getApplication());

        // XML 뷰 연결
        editTextName = findViewById(R.id.editTextName);
        editTextUserPhone = findViewById(R.id.editTextUserPhone);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // 버튼 클릭 이벤트 처리
        buttonCreateAccount.setOnClickListener(v -> createAccount());

        // 테스트 버튼 (로그인 화면으로)
        Button testButton = findViewById(R.id.button);
        testButton.setOnClickListener(v -> {
            Intent testIntent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(testIntent);
        });
    }

    private void createAccount() {
        // 🎯 입력값 가져오기
        String name = editTextName.getText().toString().trim();
        String phone = editTextUserPhone.getText().toString().trim();

        // 필수 입력값 유효성 검사
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "이름과 연락처는 필수입니다!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 이름 유효성 검사 (한글만)
        if (!name.matches("^[가-힣]{1,20}$")) {
            editTextName.setError("이름은 공백 없이 한글만 입력하세요 (1~20자)");
            return;
        }

        // 연락처 유효성 검사 (11자리 숫자)
        if (!phone.matches("^010\\d{8}$")) {
            editTextUserPhone.setError("010으로 시작하는 11자리 연락처를 입력해주세요");
            return;
        }

        // 🎯 중복 사용자 확인
        new Thread(() -> {
            try {
                boolean userExists = userRepository.isUserExists(name, phone);

                runOnUiThread(() -> {
                    if (userExists) {
                        Toast.makeText(this, "이미 가입된 사용자입니다.", Toast.LENGTH_SHORT).show();
                        editTextName.setError("이름과 연락처가 이미 등록되어 있습니다");
                        editTextUserPhone.setError("이름과 연락처가 이미 등록되어 있습니다");
                    } else {
                        // 새 사용자 생성 및 저장
                        User newUser = new User(name, phone);
                        userRepository.insertUser(newUser);

                        Log.d(TAG, "✅ 회원가입 성공: " + name + ", UUID: " + newUser.getUserId());

                        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();

                        // 로그인 화면으로 이동
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ 회원가입 처리 중 오류", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "회원가입 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}