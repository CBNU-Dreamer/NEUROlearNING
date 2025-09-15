package com.example.neurolearning.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.UUID;

@Entity(tableName = "user_table",
        indices = {
                @Index(value={"name", "phone"}, unique = true, name = "idx_login_key")
        })

public class User {

    //PK - UUID
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;

    //===========로그인 키들=======
    @NonNull
    @ColumnInfo(name = "name")
    public String name; // 한글이름 (로그인키1)

    @NonNull
    @ColumnInfo(name = "phone")
    public String phone; // 연락처 (로그인 키 2)

    @NonNull
    @ColumnInfo(name = "search_key")
    public String searchKey; // "이름#연락처" (검색용)
    //=============================

    //======추가 정보=============
    @ColumnInfo(name = "birth_year", defaultValue = "0")
    public int birthYear; // 생년 (4자리 숫자)

    @ColumnInfo(name = "birth_month", defaultValue = "0")
    public int birthMonth; // 월 (1~12)

    @ColumnInfo(name = "birth_day", defaultValue = "0")
    public int birthDay; // 일 (1~31)

    @ColumnInfo(name = "is_lunar", defaultValue = "0")
    public boolean isLunar; // 음력 여부

    @ColumnInfo(name = "guardian_phone", defaultValue = "")
    public String guardianPhone; // 보호자 연락처

    @ColumnInfo(name = "address", defaultValue = "")
    public String address; // 주소

    @ColumnInfo(name = "disease", defaultValue = "")
    public String disease; // 질병명
    //============================

    //======게임관련 필드==========
    @ColumnInfo(name = "current_story", defaultValue = "1")
    public int currentStory;

    @ColumnInfo(name = "total_completed_stories", defaultValue = "0")
    public int totalCompletedStories;
    //===============================

    @ColumnInfo(name = "last_login", defaultValue = "0")
    public long lastLogin;

    @ColumnInfo(name = "created_at", defaultValue = "0")
    public long createdAt; // 생성 시간

    // 기본 생성자 (필수)
    public User(@NonNull String name, @NonNull String phone) {
        this.userId = UUID.randomUUID().toString();
        this.name = name;
        this.phone = normalizePhoneNumber(phone);
        this.searchKey = createSearchKey(this.name, this.phone);
        this.birthYear = 0;
        this.birthMonth = 0;
        this.birthDay = 0;
        this.isLunar = false;
        this.guardianPhone = "";
        this.address = "";
        this.disease = "";
        this.currentStory = 1;
        this.totalCompletedStories = 0;
        this.lastLogin = 0;
        this.createdAt = System.currentTimeMillis();
    }

    // UserInfoActivity에서 기존 사용자 정보 업데이트용 생성자
    // UUID는 절대 변경되지 않음 - 기존 User 객체에서 정보만 업데이트할 때 사용
    public User(String existingUserId, String name, int birthYear, int birthMonth, int birthDay,
                String phone, String address, String guardianPhone, String disease, boolean isLunar) {
        this.userId = existingUserId; // 기존 UUID 유지
        this.name = name;
        this.phone = normalizePhoneNumber(phone);
        this.searchKey = createSearchKey(this.name, this.phone);
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.isLunar = isLunar;
        this.guardianPhone = guardianPhone != null ? guardianPhone : "";
        this.address = address != null ? address : "";
        this.disease = disease != null ? disease : "";
        // 게임 진행 상황과 로그인 기록은 유지되어야 함
    }

    // Getter 메서드들
    @NonNull
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public boolean isLunar() {
        return isLunar;
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

    public int getCurrentStory() {
        return currentStory;
    }

    public int getTotalCompletedStories() {
        return totalCompletedStories;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setter 메서드들 - UUID 제외
    public void setName(String name) {
        this.name = name;
        this.searchKey = createSearchKey(this.name, this.phone);
    }

    public void setPhone(String phone) {
        this.phone = normalizePhoneNumber(phone);
        this.searchKey = createSearchKey(this.name, this.phone);
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public void setIsLunar(boolean isLunar) {
        this.isLunar = isLunar;
    }

    public void setAddress(String address) {
        this.address = address != null ? address : "";
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone != null ? guardianPhone : "";
    }

    public void setDisease(String disease) {
        this.disease = disease != null ? disease : "";
    }

    public void setCurrentStory(int currentStory) {
        this.currentStory = currentStory;
    }

    public void setTotalCompletedStories(int totalCompletedStories) {
        this.totalCompletedStories = totalCompletedStories;
    }

    //===================
    // 유틸리티 메서드들
    //===================

    /**
     * 연락처 정규화 (일관된 형식으로 저장)
     */
    public static String normalizePhoneNumber(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11 && digits.startsWith("010")) {
            return digits.substring(0, 3) + "-" +
                    digits.substring(3, 7) + "-" +
                    digits.substring(7);
        }
        return phone;
    }

    /**
     * 검색 키 생성 (이름#전화번호)
     */
    public static String createSearchKey(String name, String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return name.trim() + "#" + cleanPhone;
    }

    /**
     * UserInfo 페이지에서 기존 사용자 정보 업데이트
     * UUID는 절대 변경되지 않음
     */
    public void updateUserInfo(int birthYear, int birthMonth, int birthDay,
                               boolean isLunar, String address, String guardianPhone, String disease) {
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.isLunar = isLunar;
        this.address = address != null ? address : "";
        this.guardianPhone = guardianPhone != null ? guardianPhone : "";
        this.disease = disease != null ? disease : "";
    }

    /**
     * 로그인 정보 업데이트 (이름, 전화번호)
     * UUID는 절대 변경되지 않음
     */
    public void updateLoginInfo(String name, String phone) {
        this.name = name;
        this.phone = normalizePhoneNumber(phone);
        this.searchKey = createSearchKey(this.name, this.phone);
    }

    /**
     * 로그인 기록
     */
    public void recordLogin() {
        this.lastLogin = System.currentTimeMillis();
    }


}