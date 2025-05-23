package com.example.neurolearning.ui;

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
    private EditText editTextUserPhone, editTextGuardianPhone;
    private EditText editTextAddress, editTextDisease;
    private CheckBox checkBoxLunar;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // ✅ ViewModel 연결
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // ✅ XML 뷰 연결
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextUserPhone = findViewById(R.id.editTextUserPhone);
        editTextGuardianPhone = findViewById(R.id.editTextGuardianPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextDisease = findViewById(R.id.editTextDisease);
        checkBoxLunar = findViewById(R.id.checkBoxLunar);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // ✅ 버튼 클릭 이벤트 처리
        buttonCreateAccount.setOnClickListener(v -> {
            // 🔹 입력값 가져오기
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String phone = editTextUserPhone.getText().toString().trim();
            String guardianPhone = editTextGuardianPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String disease = editTextDisease.getText().toString().trim();
            boolean isLunar = checkBoxLunar.isChecked();

            // 🔹 생년월일 (ID가 아직 없으므로 임시로 기본값)
            int year = 2000;
            int month = 1;
            int day = 1;

            // 🔹 필수 입력값 유효성 검사
            if (id.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "아이디, 비밀번호, 이름, 연락처는 필수입니다!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 선택 항목이 비어있다면 기본값("")으로 저장
            if (guardianPhone.isEmpty()) guardianPhone = "";
            if (address.isEmpty()) address = "";
            if (disease.isEmpty()) disease = "";

            // 🔹 User 객체 생성
            User user = new User(id, password, name, year, month, day, isLunar, phone, guardianPhone, address, disease);

            // 🔹 ViewModel을 통해 DB에 저장
            signUpViewModel.insertUser(user);

            Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
        });
    }
}
