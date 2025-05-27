package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;

public class Story1Activity extends AppCompatActivity {
    private static final int REQ_CROSSWORD = 101;
    private FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story1); // FrameLayout 하나 있는 XML

        contentFrame = findViewById(R.id.contentFrame);
        showInitialScreen();
    }

    private void showInitialScreen() {
        contentFrame.removeAllViews();
        View initial = LayoutInflater.from(this)
                .inflate(R.layout.activity_start_story1, contentFrame, false);

        Button btn = initial.findViewById(R.id.btnStartGame);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(Story1Activity.this, CrossWordGameActivity.class);
            startActivityForResult(intent, REQ_CROSSWORD);
        });

        contentFrame.addView(initial);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CROSSWORD && resultCode == RESULT_OK) {
            showEndScreen();
        }
    }

    private void showEndScreen() {
        contentFrame.removeAllViews();
        View end = LayoutInflater.from(this)
                .inflate(R.layout.activity_end_story1, contentFrame, false);

        Button btn = end.findViewById(R.id.btnEnd);
        btn.setOnClickListener(v -> finish());

        contentFrame.addView(end);
    }
}
