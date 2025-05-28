package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.neurolearning.R;
import com.example.neurolearning.viewmodel.UserViewModel;

public class SignInActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    private EditText editTextId;
    private EditText editTextPw;
    private Button btnLogin;
    private TextView txtFindPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // ViewModel 초기화
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // UI 요소 연결
        initViews();

        // 이벤트 리스너 설정
        setClickListeners();
    }

    private void initViews() {
        editTextId = findViewById(R.id.editTextId);
        editTextPw = findViewById(R.id.editTextPw);
        btnLogin = findViewById(R.id.btnLogin);
        txtFindPw = findViewById(R.id.txtFindPw);
    }

    private void setClickListeners() {
        // 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(v -> performLogin());

        // 비밀번호 찾기 텍스트 클릭 이벤트 (선택사항)
        txtFindPw.setOnClickListener(v -> {
            Toast.makeText(this, "비밀번호 찾기 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
            // TODO: 비밀번호 찾기 기능 구현 시 해당 Activity로 이동
        });
    }

    private void performLogin() {
        String username = editTextId.getText().toString().trim();
        String password = editTextPw.getText().toString().trim();

        // 입력값 유효성 검사
        if (username.isEmpty()) {
            editTextId.setError("아이디를 입력해주세요.");
            editTextId.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPw.setError("비밀번호를 입력해주세요.");
            editTextPw.requestFocus();
            return;
        }

        // DB에서 사용자 정보 조회
        userViewModel.getUserByUsername(username).observe(this, user -> {
            if (user != null) {
                // 사용자가 존재하는 경우 비밀번호 확인
                if (user.password.equals(password)) {
                    // 로그인 성공
                    Toast.makeText(this, user.name + "님, 환영합니다!", Toast.LENGTH_SHORT).show();

                    // 메인 화면 또는 스토리 화면으로 이동
                    Intent intent = new Intent(SignInActivity.this, StoryActivity.class);
                    // 사용자 정보를 다음 Activity로 전달 (필요한 경우)
                    intent.putExtra("username", user.username);
                    intent.putExtra("name", user.name);
                    startActivity(intent);
                    finish(); // 로그인 화면 종료
                } else {
                    // 비밀번호 불일치
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    editTextPw.setError("비밀번호를 다시 확인해주세요.");
                    editTextPw.requestFocus();
                }
            } else {
                // 사용자가 존재하지 않는 경우
                Toast.makeText(this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                editTextId.setError("아이디를 다시 확인해주세요.");
                editTextId.requestFocus();
            }
        });
    }

}