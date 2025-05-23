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
}
