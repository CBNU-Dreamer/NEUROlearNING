package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class Story2Activity extends AppCompatActivity {
    private static final int REQ_KIOSK = 100;
    private FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story2);

        contentFrame = findViewById(R.id.contentFrame);

        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story2, contentFrame, false);
        Button btn = initial.findViewById(R.id.btnStartGame);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(Story2Activity.this, KioskGameActivity.class);
            startActivityForResult(intent, REQ_KIOSK);
        });

        contentFrame.addView(initial);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_KIOSK && resultCode == RESULT_OK) {
            showEndScreen();
        } else if (requestCode == REQ_KIOSK) {
            Toast.makeText(this, "게임이 정상 종료되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEndScreen() {
        contentFrame.removeAllViews();
        View end = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story2, contentFrame, false);
        Button btn = end.findViewById(R.id.btnEnd);

        btn.setOnClickListener(v -> finish());
        contentFrame.addView(end);
    }
}
