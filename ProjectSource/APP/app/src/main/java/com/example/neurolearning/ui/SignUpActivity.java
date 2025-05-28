package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;

    private EditText editTextId, editTextPassword, editTextName;
    private EditText editTextBirthYear, editTextBirthMonth, editTextBirthDay;
    private EditText editTextUserPhone, editTextGuardianPhone;
    private EditText editTextAddress, editTextDisease;
    private CheckBox checkBoxLunar;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // ViewModel 연결
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // XML 뷰 연결
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextBirthYear = findViewById(R.id.editTextBirthYear);
        editTextBirthMonth = findViewById(R.id.editTextBirthMonth);
        editTextBirthDay = findViewById(R.id.editTextBirthDay);
        editTextUserPhone = findViewById(R.id.editTextUserPhone);
        editTextGuardianPhone = findViewById(R.id.editTextGuardianPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextDisease = findViewById(R.id.editTextDisease);
        checkBoxLunar = findViewById(R.id.checkBoxLunar);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // 버튼 클릭 이벤트 처리
        buttonCreateAccount.setOnClickListener(v -> {
            // 🔹 입력값 가져오기
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String strYear = editTextBirthYear.getText().toString().trim();
            String strMonth = editTextBirthMonth.getText().toString().trim();
            String strDay = editTextBirthDay.getText().toString().trim();
            String phone = editTextUserPhone.getText().toString().trim();
            String guardianPhone = editTextGuardianPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String disease = editTextDisease.getText().toString().trim();
            boolean isLunar = checkBoxLunar.isChecked();

            // 필수 입력값 유효성 검사
            if (id.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "아이디, 비밀번호, 이름, 연락처는 필수입니다!", Toast.LENGTH_SHORT).show();
                return;
            }



            // 선택 항목이 비어있다면 기본값("")으로 저장
            if (guardianPhone.isEmpty()) guardianPhone = "";
            if (address.isEmpty()) address = "";
            if (disease.isEmpty()) disease = "";

            //id조건확인
            if (!id.matches("^[a-z0-9]{8,12}$")) {
                editTextId.setError("아이디는 영어 소문자와 숫자 조합의 8~12자여야 합니다.");
                return;
            }

            //비밀번호 조건확인
            if (!password.matches("^[a-z0-9]{8,12}$")) {
                editTextPassword.setError("비밀번호는 영어 소문자와 숫자 조합의 8~12자여야 합니다.");
                return;
            }

            if (!name.matches("^[가-힣]{1,20}$")) {
                editTextName.setError("이름은 공백 없이 한글만 입력하세요.");
                return;
            }

            int year = 0, month = 0, day = 0;

            try {
                year = Integer.parseInt(strYear);
                month = Integer.parseInt(strMonth);
                day = Integer.parseInt(strDay);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "생년월일은 숫자로 입력해주세요!", Toast.LENGTH_SHORT).show();
                return; // 가입 중단
            }

            // 생년월일 범위 조건 확인
            if (year < 1900 || year > 2025) {
                editTextBirthYear.setError("올바른 연도(1900~2025)를 입력하세요.");
                return;
            }

            if (month < 1 || month > 12) {
                editTextBirthMonth.setError("1~12 사이의 월을 입력하세요.");
                return;
            }

            if (day < 1 || day > 31) {
                editTextBirthDay.setError("1~31 사이의 일을 입력하세요.");
                return;
            }


            //연락처 조건확인
            if (phone.length() > 20) {
                editTextUserPhone.setError("20자 이내로 입력해주세요.");
                return;
            }
            if (!guardianPhone.isEmpty() && guardianPhone.length() > 20) {
                editTextGuardianPhone.setError("20자 이내로 입력해주세요.");
                return;
            }


            // User 객체 생성
            User user = new User(id, password, name, year, month, day, isLunar, phone, guardianPhone, address, disease);

            // ViewModel을 통해 DB에 저장
            signUpViewModel.insertUser(user);

            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

            Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
        });

        Button testButton = findViewById(R.id.button);
        testButton.setOnClickListener(v -> {
            Intent testintent = new Intent(SignUpActivity.this, StoryActivity.class);
            startActivity(testintent);
        });

    }
}
