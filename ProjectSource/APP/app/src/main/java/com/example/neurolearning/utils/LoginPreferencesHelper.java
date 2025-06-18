package com.example.neurolearning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 최근 로그인한 사용자 정보를 저장/불러오는 헬퍼 클래스
 */
public class LoginPreferencesHelper {
    private static final String TAG = "LoginPrefsHelper";
    private static final String PREF_NAME = "login_preferences";
    private static final String KEY_LAST_LOGIN_NAME = "last_login_name";
    private static final String KEY_LAST_LOGIN_PHONE = "last_login_phone";
    private static final String KEY_HAS_RECENT_LOGIN = "has_recent_login";

    private final SharedPreferences preferences;

    public LoginPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 최근 로그인 정보 저장
     */
    public void saveLastLoginInfo(String name, String phone) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_LAST_LOGIN_NAME, name);
            editor.putString(KEY_LAST_LOGIN_PHONE, phone);
            editor.putBoolean(KEY_HAS_RECENT_LOGIN, true);
            editor.apply();

            Log.d(TAG, "최근 로그인 정보 저장: " + name + ", " + phone);
        } catch (Exception e) {
            Log.e(TAG, "최근 로그인 정보 저장 실패", e);
        }
    }

    /**
     * 최근 로그인한 사용자 이름 가져오기
     */
    public String getLastLoginName() {
        return preferences.getString(KEY_LAST_LOGIN_NAME, "");
    }

    /**
     * 최근 로그인한 사용자 전화번호 가져오기
     */
    public String getLastLoginPhone() {
        return preferences.getString(KEY_LAST_LOGIN_PHONE, "");
    }

    /**
     * 최근 로그인 정보가 있는지 확인
     */
    public boolean hasRecentLogin() {
        return preferences.getBoolean(KEY_HAS_RECENT_LOGIN, false) &&
                !getLastLoginName().isEmpty() &&
                !getLastLoginPhone().isEmpty();
    }

    /**
     * 최근 로그인 정보 삭제 (로그아웃 시 사용)
     */
    public void clearLastLoginInfo() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(KEY_LAST_LOGIN_NAME);
            editor.remove(KEY_LAST_LOGIN_PHONE);
            editor.putBoolean(KEY_HAS_RECENT_LOGIN, false);
            editor.apply();

            Log.d(TAG, "최근 로그인 정보 삭제 완료");
        } catch (Exception e) {
            Log.e(TAG, "최근 로그인 정보 삭제 실패", e);
        }
    }

    /**
     * 최근 로그인 정보를 LoginInfo 객체로 반환
     */
    public LoginInfo getLastLoginInfo() {
        if (hasRecentLogin()) {
            return new LoginInfo(getLastLoginName(), getLastLoginPhone());
        }
        return null;
    }

    /**
     * 로그인 정보를 담는 간단한 데이터 클래스
     */
    public static class LoginInfo {
        public final String name;
        public final String phone;

        public LoginInfo(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "LoginInfo{name='" + name + "', phone='" + phone + "'}";
        }
    }
}