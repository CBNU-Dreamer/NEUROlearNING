package com.example.neurolearning.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.viewmodel.UserViewModel;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etUserId, etPassword, etName, etAge, etPhone, etAddress, etGuardian, etDisease;
    private ImageView imageView;
    private Button btnUploadPhoto, btnModify;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        // ViewModel 초기화
        userViewModel = new UserViewModel(getApplication());

        // 뷰 바인딩
        etUserId = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etGuardian = findViewById(R.id.etGuardian);
        etDisease = findViewById(R.id.etDisease);

        imageView = findViewById(R.id.imageView5);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnModify = findViewById(R.id.btnModify);

        String username = getIntent().getStringExtra("username");
        loadUserInfo(username);

        // 아이디는 수정 불가
        etUserId.setEnabled(false);

        // 사용자 데이터 로딩 (가정: 아이디는 이미 로그인한 사용자라면 전달됨)
        //loadUserInfo("temp0004");  // 나중에 intent나 SharedPref로 대체

        // 수정 버튼 클릭 시
        btnModify.setOnClickListener(v -> updateUserInfo());

        // 사진 업로드 버튼 클릭 시 (기능 보류 가능)
        btnUploadPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "사진 업로드 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo(String userId) {
        User user = userViewModel.getUserById(userId);
        if (user != null) {
            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText(user.getName() + "님의 정보입니다!");

            etUserId.setText(user.getId());
            etPassword.setText(user.getPassword());
            etName.setText(user.getName());
            etAge.setText(String.valueOf(user.getAge()));
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
            etGuardian.setText(user.getGuardianPhone());
            etDisease.setText(user.getDisease());
        }
    }


    private void updateUserInfo() {
        String id = etUserId.getText().toString();
        String password = etPassword.getText().toString();
        String name = etName.getText().toString();
        String ageStr = etAge.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String guardian = etGuardian.getText().toString();
        String disease = etDisease.getText().toString();

        // 유효성 검사
        if (!validateInput(password, name, ageStr, phone)) return;

        int age = Integer.parseInt(ageStr);

        User user = new User(id, password, name, age, phone, address, guardian, disease);
        userViewModel.updateUser(user);

        Toast.makeText(this, "정보가 수정되었습니다!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput(String pw, String name, String ageStr, String phone) {
        if (TextUtils.isEmpty(pw) || pw.length() < 8 || pw.length() > 12 || pw.matches(".*[^a-z0-9].*")) {
            etPassword.setError("비밀번호는 8~12자, 영문소문자+숫자만 허용");
            return false;
        }
        if (!name.matches("^[가-힣]{1,}$")) {
            etName.setError("이름은 한글만 입력하세요");
            return false;
        }
        if (!ageStr.matches("\\d{1,2}")) {
            etAge.setError("나이는 숫자만 입력");
            return false;
        }
        if (!phone.matches("^\\d{3}-\\d{3,4}-\\d{4}$")) {
            etPhone.setError("올바른 연락처 형식: 000-0000-0000");
            return false;
        }
        return true;
    }
}