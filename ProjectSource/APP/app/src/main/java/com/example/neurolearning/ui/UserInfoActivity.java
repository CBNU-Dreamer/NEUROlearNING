package com.example.neurolearning.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoActivity";

    private UserRepository userRepository;
    private User currentUser;
    private String currentUserId;

    // UI 요소들 - XML ID에 맞춤
    private TextView tvTitle;
    private EditText etName, etPhone, etBirthYear, etBirthMonth, etBirthDay;
    private EditText etAddress, etGuardian, etDisease;
    private CheckBox checkBoxLunar;
    private ImageView imageView5;
    private Button btnUploadPhoto, btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Log.d(TAG, "UserInfoActivity onCreate 시작");

        try {
            // Repository 초기화
            userRepository = new UserRepository(getApplication());

            // 사용자 ID 받기
            currentUserId = getIntent().getStringExtra("userId");
            if (currentUserId == null) {
                Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "사용자 ID: " + currentUserId);

            // 툴바 설정
            setupToolbar();

            // 뷰 바인딩
            initViews();

            // 사용자 정보 로드
            loadUserInfo();

            // 버튼 리스너 설정
            setupButtonListeners();

            Log.d(TAG, "UserInfoActivity 초기화 완료");

        } catch (Exception e) {
            Log.e(TAG, "UserInfoActivity onCreate 중 오류", e);
            Toast.makeText(this, "초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        try {
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> finish());
                Log.d(TAG, "툴바 설정 완료");
            }
        } catch (Exception e) {
            Log.e(TAG, "툴바 설정 실패", e);
        }
    }

    private void initViews() {
        try {
            // XML에 정의된 ID들과 정확히 매칭
            tvTitle = findViewById(R.id.tvTitle);
            etName = findViewById(R.id.etName);
            etPhone = findViewById(R.id.etPhone);
            etBirthYear = findViewById(R.id.etBirthYear);
            etBirthMonth = findViewById(R.id.etBirthMonth);
            etBirthDay = findViewById(R.id.etBirthDay);
            etAddress = findViewById(R.id.etAddress);
            etGuardian = findViewById(R.id.etGuardian);
            etDisease = findViewById(R.id.etDisease);
            checkBoxLunar = findViewById(R.id.checkBoxLunar);
            imageView5 = findViewById(R.id.imageView5);
            btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
            btnModify = findViewById(R.id.btnModify);

            Log.d(TAG, "뷰 초기화 완료");
        } catch (Exception e) {
            Log.e(TAG, "뷰 초기화 실패", e);
            throw e;
        }
    }

    private void setupButtonListeners() {
        try {
            if (btnUploadPhoto != null) {
                btnUploadPhoto.setOnClickListener(v -> {
                    Toast.makeText(this, "사진 업로드 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
                });
            }

            if (btnModify != null) {
                btnModify.setOnClickListener(v -> updateUserInfo());
            }

            Log.d(TAG, "버튼 리스너 설정 완료");
        } catch (Exception e) {
            Log.e(TAG, "버튼 리스너 설정 실패", e);
        }
    }

    private void loadUserInfo() {
        new Thread(() -> {
            try {
                currentUser = userRepository.getUserById(currentUserId);

                runOnUiThread(() -> {
                    if (currentUser != null) {
                        Log.d(TAG, "✅ 사용자 정보 로드 성공: " + currentUser.getName());
                        displayUserInfo();
                    } else {
                        Log.e(TAG, "❌ 사용자 정보를 찾을 수 없음: " + currentUserId);
                        Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 정보 로드 실패", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "사용자 정보 로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void displayUserInfo() {
        try {
            // 🎯 타이틀 업데이트
            if (tvTitle != null) {
                tvTitle.setText(currentUser.getName() + "님의 정보입니다!");
            }

            // 🎯 기존 값을 hint로 표시, 실제 값은 빈 상태로 시작
            if (etName != null) {
                etName.setHint("현재: " + currentUser.getName());
            }

            if (etPhone != null) {
                etPhone.setHint("현재: " + currentUser.getPhone());
            }

            // 생년월일 처리
            if (etBirthYear != null) {
                if (currentUser.getBirthYear() > 0) {
                    etBirthYear.setHint("현재: " + currentUser.getBirthYear());
                } else {
                    etBirthYear.setHint("생년 (예: 1990)");
                }
            }

            if (etBirthMonth != null) {
                if (currentUser.getBirthMonth() > 0) {
                    etBirthMonth.setHint("현재: " + currentUser.getBirthMonth());
                } else {
                    etBirthMonth.setHint("월 (1~12)");
                }
            }

            if (etBirthDay != null) {
                if (currentUser.getBirthDay() > 0) {
                    etBirthDay.setHint("현재: " + currentUser.getBirthDay());
                } else {
                    etBirthDay.setHint("일 (1~31)");
                }
            }

            if (etAddress != null) {
                etAddress.setHint("현재: " + (currentUser.getAddress().isEmpty() ? "미입력" : currentUser.getAddress()));
            }

            if (etGuardian != null) {
                etGuardian.setHint("현재: " + (currentUser.getGuardianPhone().isEmpty() ? "미입력" : currentUser.getGuardianPhone()));
            }

            if (etDisease != null) {
                etDisease.setHint("현재: " + (currentUser.getDisease().isEmpty() ? "미입력" : currentUser.getDisease()));
            }

            if (checkBoxLunar != null) {
                checkBoxLunar.setChecked(currentUser.isLunar());
            }

            Log.d(TAG, "사용자 정보 표시 완료");
        } catch (Exception e) {
            Log.e(TAG, "사용자 정보 표시 실패", e);
            Toast.makeText(this, "사용자 정보 표시 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 🎯 입력된 값 가져오기 (빈 값이면 기존 값 유지)
            String name = getUpdatedValue(etName != null ? etName.getText().toString() : "", currentUser.getName());
            String phone = getUpdatedValue(etPhone != null ? etPhone.getText().toString() : "", currentUser.getPhone());

            // 생년월일 처리
            int birthYear = getUpdatedIntValue(etBirthYear != null ? etBirthYear.getText().toString() : "", currentUser.getBirthYear());
            int birthMonth = getUpdatedIntValue(etBirthMonth != null ? etBirthMonth.getText().toString() : "", currentUser.getBirthMonth());
            int birthDay = getUpdatedIntValue(etBirthDay != null ? etBirthDay.getText().toString() : "", currentUser.getBirthDay());

            String address = getUpdatedValue(etAddress != null ? etAddress.getText().toString() : "", currentUser.getAddress());
            String guardian = getUpdatedValue(etGuardian != null ? etGuardian.getText().toString() : "", currentUser.getGuardianPhone());
            String disease = getUpdatedValue(etDisease != null ? etDisease.getText().toString() : "", currentUser.getDisease());
            boolean isLunar = checkBoxLunar != null ? checkBoxLunar.isChecked() : false;

            // 🎯 유효성 검사
            if (!validateInput(name, phone, birthYear, birthMonth, birthDay)) {
                return;
            }

            // 🎯 새로운 User 객체 생성 (기존 UUID 유지)
            User updatedUser = new User(
                    currentUser.getUserId(), // 기존 UUID 유지
                    name, birthYear, birthMonth, birthDay,
                    phone, address, guardian, disease, isLunar
            );

            // 🎯 게임 진행 정보는 기존 값 유지
            updatedUser.setCurrentStory(currentUser.getCurrentStory());
            updatedUser.setTotalCompletedStories(currentUser.getTotalCompletedStories());

            // DB 업데이트
            new Thread(() -> {
                try {
                    userRepository.updateUser(updatedUser);
                    currentUser = updatedUser; // 현재 사용자 정보 업데이트

                    runOnUiThread(() -> {
                        Toast.makeText(this, "정보가 수정되었습니다!", Toast.LENGTH_LONG).show();
                        clearInputFieldsAndUpdateHints();
                    });

                    Log.d(TAG, "✅ 사용자 정보 업데이트 성공: " + name);
                } catch (Exception e) {
                    Log.e(TAG, "❌ 사용자 정보 업데이트 실패", e);
                    runOnUiThread(() ->
                            Toast.makeText(this, "정보 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "❌ 입력값 처리 중 오류", e);
            Toast.makeText(this, "입력값을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 🎯 새 값이 있으면 새 값, 없으면 기존 값 반환
    private String getUpdatedValue(String newValue, String currentValue) {
        String trimmedNew = newValue != null ? newValue.trim() : "";
        return trimmedNew.isEmpty() ? (currentValue != null ? currentValue : "") : trimmedNew;
    }

    // 🎯 정수 값 업데이트 처리
    private int getUpdatedIntValue(String newValue, int currentValue) {
        String trimmed = newValue != null ? newValue.trim() : "";
        if (trimmed.isEmpty()) {
            return currentValue;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return currentValue;
        }
    }

    // 🎯 유효성 검사
    private boolean validateInput(String name, String phone, int birthYear, int birthMonth, int birthDay) {
        // 이름 검사
        if (!name.matches("^[가-힣]{1,20}$")) {
            if (etName != null) etName.setError("이름은 한글만 1~20자로 입력하세요");
            return false;
        }

        // 전화번호 검사
        if (!phone.matches("^010-\\d{4}-\\d{4}$")) {
            if (etPhone != null) etPhone.setError("올바른 연락처 형식: 010-1234-5678");
            return false;
        }

        // 생년월일 검사 (입력된 경우만)
        if (birthYear > 0) {
            if (birthYear < 1900 || birthYear > 2025) {
                if (etBirthYear != null) etBirthYear.setError("올바른 연도(1900~2025)를 입력하세요");
                return false;
            }
            if (birthMonth < 1 || birthMonth > 12) {
                if (etBirthMonth != null) etBirthMonth.setError("1~12 사이의 월을 입력하세요");
                return false;
            }
            if (birthDay < 1 || birthDay > 31) {
                if (etBirthDay != null) etBirthDay.setError("1~31 사이의 일을 입력하세요");
                return false;
            }
        }

        return true;
    }

    // 🎯 입력 필드 초기화 및 hint 업데이트
    private void clearInputFieldsAndUpdateHints() {
        try {
            if (tvTitle != null) {
                tvTitle.setText(currentUser.getName() + "님의 정보입니다!");
            }

            if (etName != null) {
                etName.setText("");
                etName.setHint("현재: " + currentUser.getName());
                etName.setError(null);
            }

            if (etPhone != null) {
                etPhone.setText("");
                etPhone.setHint("현재: " + currentUser.getPhone());
                etPhone.setError(null);
            }

            if (etBirthYear != null) {
                etBirthYear.setText("");
                etBirthYear.setHint("현재: " + (currentUser.getBirthYear() > 0 ? currentUser.getBirthYear() : "미입력"));
                etBirthYear.setError(null);
            }

            if (etBirthMonth != null) {
                etBirthMonth.setText("");
                etBirthMonth.setHint("현재: " + (currentUser.getBirthMonth() > 0 ? currentUser.getBirthMonth() : "미입력"));
                etBirthMonth.setError(null);
            }

            if (etBirthDay != null) {
                etBirthDay.setText("");
                etBirthDay.setHint("현재: " + (currentUser.getBirthDay() > 0 ? currentUser.getBirthDay() : "미입력"));
                etBirthDay.setError(null);
            }

            if (etAddress != null) {
                etAddress.setText("");
                etAddress.setHint("현재: " + (currentUser.getAddress().isEmpty() ? "미입력" : currentUser.getAddress()));
            }

            if (etGuardian != null) {
                etGuardian.setText("");
                etGuardian.setHint("현재: " + (currentUser.getGuardianPhone().isEmpty() ? "미입력" : currentUser.getGuardianPhone()));
            }

            if (etDisease != null) {
                etDisease.setText("");
                etDisease.setHint("현재: " + (currentUser.getDisease().isEmpty() ? "미입력" : currentUser.getDisease()));
            }

            if (checkBoxLunar != null) {
                checkBoxLunar.setChecked(currentUser.isLunar());
            }

            Log.d(TAG, "입력 필드 초기화 완료");
        } catch (Exception e) {
            Log.e(TAG, "입력 필드 초기화 실패", e);
        }
    }
}