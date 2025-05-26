package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    public String username;  // 아이디 (8~12자, 소문자+숫자)

    @ColumnInfo(name = "password")
    public String password;  // 비밀번호 (8~12자, 소문자+숫자)

    @ColumnInfo(name = "name")
    public String name;      // 이름 (한글만)

    @ColumnInfo(name = "birth_year")
    public int birthYear;    // 생년 (4자리 숫자)

    @ColumnInfo(name = "birth_month")
    public int birthMonth;   // 월 (1~12)

    @ColumnInfo(name = "birth_day")
    public int birthDay;     // 일 (1~31)

    @ColumnInfo(name = "isLunar")
    public boolean isLunar;  // 음력 여부 (체크박스)

    @ColumnInfo(name = "phone")
    public String phone;     // 연락처 (20자 이내)

    @ColumnInfo(name = "guardianPhone")
    public String guardianPhone;  // 보호자 연락처 (20자 이내)

    @ColumnInfo(name = "address")
    public String address;   // 주소

    @ColumnInfo(name = "disease")
    public String disease;   // (선택) 질병명

    // 기본 생성자 (필수)
    public User(@NonNull String username, String password, String name,
                int birthYear, int birthMonth, int birthDay, boolean isLunar,
                String phone, String guardianPhone, String address, String disease) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.isLunar = isLunar;
        this.phone = phone;
        this.guardianPhone = guardianPhone;
        this.address = address;
        this.disease = disease;
    }

    // UserInfoActivity에서 사용하는 생성자 (나이 기반)
    public User(@NonNull String username, String password, String name, int age,
                String phone, String address, String guardianPhone, String disease) {
        this.username = username;
        this.password = password;
        this.name = name;
        // 나이를 대략적인 생년으로 변환 (2024년 기준)
        this.birthYear = 2024 - age;
        this.birthMonth = 1;  // 기본값
        this.birthDay = 1;    // 기본값
        this.isLunar = false; // 기본값
        this.phone = phone;
        this.guardianPhone = guardianPhone;
        this.address = address;
        this.disease = disease;
    }

    // Getter 메서드들 추가
    @NonNull
    public String getId() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        // 2024년 기준으로 나이 계산
        return 2024 - birthYear;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public String getGuardianPhone() {
        return guardianPhone != null ? guardianPhone : "";
    }

    public String getDisease() {
        return disease != null ? disease : "";
    }

    // Setter 메서드들 (업데이트용)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.birthYear = 2024 - age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}