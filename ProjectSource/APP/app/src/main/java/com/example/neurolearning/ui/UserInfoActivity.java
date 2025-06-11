package com.example.neurolearning.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.viewmodel.UserViewModel;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etUserId, etPassword, etName, etAge, etPhone, etAddress, etGuardian, etDisease;
    private ImageView imageView;
    private Button btnUploadPhoto, btnModify;
    private UserViewModel userViewModel;

    // 🎯 기존 사용자 정보 저장용
    private User currentUser;

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
        //loadUserInfo("yourid");  // 나중에 intent나 SharedPref로 대체

        // 수정 버튼 클릭 시
        btnModify.setOnClickListener(v -> updateUserInfo());

        // 사진 업로드 버튼 클릭 시 (기능 보류 가능)
        btnUploadPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "사진 업로드 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo(String userId) {
        // 🎯 DB에서 사용자 정보 가져와서 currentUser에 저장
        currentUser = userViewModel.getUserById(userId);
        if (currentUser != null) {
            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText(currentUser.getName() + "님의 정보입니다!");
            // 🎯 setText 대신 setHint 사용 - 기존 값이 placeholder로 표시됨
            etUserId.setText(currentUser.getId());
            etPassword.setHint("현재: " + currentUser.getPassword());
            etName.setHint("현재: " + currentUser.getName());
            etAge.setHint("현재: " + currentUser.getAge() + "세");
            etPhone.setHint("현재: " + currentUser.getPhone());
            etAddress.setHint("현재: " + currentUser.getAddress());
            etGuardian.setHint("현재: " + currentUser.getGuardianPhone());
            etDisease.setHint("현재: " + currentUser.getDisease());
        }
    }


    private void updateUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🎯 입력된 값 가져오기 (빈 값이면 기존 값 유지)
        String id = currentUser.getId(); // 아이디는 변경 불가
        String password = getUpdatedValue(etPassword.getText().toString(), currentUser.getPassword());
        String name = getUpdatedValue(etName.getText().toString(), currentUser.getName());
        String ageStr = etAge.getText().toString().trim();
        String phone = getUpdatedValue(etPhone.getText().toString(), currentUser.getPhone());
        String address = getUpdatedValue(etAddress.getText().toString(), currentUser.getAddress());
        String guardian = getUpdatedValue(etGuardian.getText().toString(), currentUser.getGuardianPhone());
        String disease = getUpdatedValue(etDisease.getText().toString(), currentUser.getDisease());

        // 🎯 나이 처리 (빈 값이면 기존 나이 유지)
        int age;
        if (ageStr.isEmpty()) {
            age = currentUser.getAge(); // 기존 나이 유지
        } else {
            // 나이 유효성 검사
            if (!ageStr.matches("\\d{1,3}")) {
                etAge.setError("나이는 숫자만 입력하세요 (1~999)");
                return;
            }
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 150) {
                etAge.setError("올바른 나이를 입력하세요 (1~150)");
                return;
            }
        }

        // 🎯 수정된 부분만 유효성 검사
        if (!validateChangedFields(password, name, phone)) {
            return;
        }

        // 🎯 사용자 정보 업데이트
        User updatedUser = new User(id, password, name, age, phone, address, guardian, disease);
        userViewModel.updateUser(updatedUser);

        // 🎯 currentUser도 업데이트
        currentUser = updatedUser;

        Toast.makeText(this, "정보가 수정되었습니다!", Toast.LENGTH_LONG).show();

        // 🎯 수정 후 입력 필드 초기화 및 hint 업데이트
        clearInputFieldsAndUpdateHints();
    }

    // 🎯 새 값이 있으면 새 값, 없으면 기존 값 반환
    private String getUpdatedValue(String newValue, String currentValue) {
        String trimmedNew = newValue != null ? newValue.trim() : "";
        return trimmedNew.isEmpty() ? (currentValue != null ? currentValue : "") : trimmedNew;
    }

    // 🎯 수정된 필드만 유효성 검사
    private boolean validateChangedFields(String password, String name, String phone) {
        // 비밀번호가 변경되었으면 검사
        if (!password.equals(currentUser.getPassword())) {
            if (password.length() < 8 || password.length() > 12 ||
                    !password.matches("^[a-z0-9]+$")) {
                etPassword.setError("비밀번호는 8~12자, 영문소문자+숫자만 허용");
                return false;
            }
        }

        // 이름이 변경되었으면 검사
        if (!name.equals(currentUser.getName())) {
            if (!name.matches("^[가-힣]+$")) {
                etName.setError("이름은 한글만 입력하세요");
                return false;
            }
        }

        // 전화번호가 변경되었으면 검사
        if (!phone.equals(currentUser.getPhone())) {
            if (!phone.isEmpty() && !phone.matches("^\\d{3}-\\d{3,4}-\\d{4}$")) {
                etPhone.setError("올바른 연락처 형식: 000-0000-0000");
                return false;
            }
        }

        return true;
    }

    // 🎯 입력 필드 초기화 및 hint 업데이트
    private void clearInputFieldsAndUpdateHints() {
        etPassword.setText("");
        etPassword.setHint("현재: " + currentUser.getPassword());

        etName.setText("");
        etName.setHint("현재: " + currentUser.getName());

        etAge.setText("");
        etAge.setHint("현재: " + currentUser.getAge() + "세");

        etPhone.setText("");
        etPhone.setHint("현재: " + currentUser.getPhone());

        etAddress.setText("");
        etAddress.setHint("현재: " + currentUser.getAddress());

        etGuardian.setText("");
        etGuardian.setHint("현재: " + currentUser.getGuardianPhone());

        etDisease.setText("");
        etDisease.setHint("현재: " + currentUser.getDisease());

        // 오류 메시지 제거
        etPassword.setError(null);
        etName.setError(null);
        etAge.setError(null);
        etPhone.setError(null);
    }
}