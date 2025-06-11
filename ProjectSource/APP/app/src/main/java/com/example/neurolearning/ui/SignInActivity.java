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
    private TextView tvGoToSignUp; // 🎯 회원가입 링크 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_signin);
            Log.d(TAG, "SignInActivity 시작");

            // Repository 초기화
            userRepository = new UserRepository(getApplication());

            // UI 요소 연결
            initViews();

            // 이벤트 리스너 설정
            setClickListeners();

            // 뒤로가기 콜백 설정
            setupBackPressedCallback();

            Log.d(TAG, "SignInActivity 초기화 완료");

        } catch (Exception e) {
            Log.e(TAG, "❌ SignInActivity 초기화 중 오류", e);
            Toast.makeText(this, "로그인 화면을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        try {
            editTextName = findViewById(R.id.editTextName);
            editTextPhone = findViewById(R.id.editTextPhone);
            btnLogin = findViewById(R.id.btnLogin);
            tvGoToSignUp = findViewById(R.id.tvGoToSignUp); // 🎯 회원가입 링크 연결

            Log.d(TAG, "UI 요소 연결 완료");

            // 🎯 null 체크
            if (editTextName == null) {
                Log.e(TAG, "❌ editTextName이 null입니다. activity_signin.xml을 확인하세요.");
            }
            if (editTextPhone == null) {
                Log.e(TAG, "❌ editTextPhone이 null입니다. activity_signin.xml을 확인하세요.");
            }
            if (btnLogin == null) {
                Log.e(TAG, "❌ btnLogin이 null입니다. activity_signin.xml을 확인하세요.");
            }
            if (tvGoToSignUp == null) {
                Log.e(TAG, "❌ tvGoToSignUp이 null입니다. activity_signin.xml을 확인하세요.");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ UI 요소 연결 중 오류", e);
            throw e;
        }
    }

    private void setClickListeners() {
        try {
            // 로그인 버튼 클릭 이벤트
            if (btnLogin != null) {
                btnLogin.setOnClickListener(v -> performLogin());
            }

            // 🎯 회원가입 링크 클릭 이벤트
            if (tvGoToSignUp != null) {
                tvGoToSignUp.setOnClickListener(v -> {
                    Log.d(TAG, "회원가입 텍스트 클릭 - SignUpActivity로 이동");
                    navigateToSignUp();
                });
            } else {
                Log.w(TAG, "⚠️ tvGoToSignUp이 null입니다. 회원가입 링크가 XML에 없을 수 있습니다.");
            }

            Log.d(TAG, "클릭 리스너 설정 완료");

        } catch (Exception e) {
            Log.e(TAG, "❌ 클릭 리스너 설정 중 오류", e);
        }
    }

    private void performLogin() {
        try {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            Log.d(TAG, "로그인 시도: " + name + ", " + phone);

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
                    User user = userRepository.loginUser(name, phone);

                    runOnUiThread(() -> {
                        if (user != null) {
                            // 로그인 성공
                            Log.d(TAG, "✅ 로그인 성공: " + user.getName() + " (UUID: " + user.getUserId() + ")");
                            Toast.makeText(this, user.getName() + "님, 환영합니다!", Toast.LENGTH_SHORT).show();

                            // StoryActivity로 이동 (사용자 ID 전달)
                            navigateToStory(user);
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

        } catch (Exception e) {
            Log.e(TAG, "❌ performLogin 중 오류", e);
            Toast.makeText(this, "로그인 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎯 StoryActivity로 안전하게 이동
     */
    private void navigateToStory(User user) {
        try {
            Intent intent = new Intent(SignInActivity.this, StoryActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("userName", user.getName());

            // 기존 액티비티 스택을 모두 제거하고 새로운 태스크로 시작
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();

            Log.d(TAG, "StoryActivity로 이동 완료");

        } catch (Exception e) {
            Log.e(TAG, "❌ StoryActivity 이동 중 오류", e);
            Toast.makeText(this, "화면 이동 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🎯 SignUpActivity로 안전하게 이동
     */
    private void navigateToSignUp() {
        try {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Log.d(TAG, "SignUpActivity로 이동 완료");

        } catch (Exception e) {
            Log.e(TAG, "❌ SignUpActivity 이동 중 오류", e);
            Toast.makeText(this, "화면 이동 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 🎯 뒤로가기 버튼 처리
    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 🎯 첫 화면이므로 앱 종료
                finishAffinity(); // 앱의 모든 액티비티 종료
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SignInActivity 종료");
    }
}