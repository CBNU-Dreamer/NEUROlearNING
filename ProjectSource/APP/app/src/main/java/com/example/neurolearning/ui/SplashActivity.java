package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 3000; // 3초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "스플래시 화면 시작");

        // 로고 이미지에 페이드인 애니메이션 적용
        ImageView logoImage = findViewById(R.id.ivLogo);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000); // 1초 동안 페이드인
        logoImage.startAnimation(fadeIn);

        // 3초 후 SignInActivity로 이동
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

            Log.d(TAG, "SignInActivity로 이동");
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        // 스플래시 화면에서는 뒤로가기 무시
    }
}