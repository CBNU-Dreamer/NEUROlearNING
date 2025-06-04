package com.example.neurolearning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class NeighborBookActivity extends AppCompatActivity {
    private static final String TAG = "NeighborBookActivity";

    private UserRepository userRepository;
    private String currentUserId;
    private String currentUserName;
    private int unlockedStoryCount = 1; // 기본값 1개

    // 데이터 모델
    static class Neighbor {
        int imageRes;
        String name;
        String desc;
        Neighbor(int imageRes, String name, String desc) {
            this.imageRes = imageRes;
            this.name = name;
            this.desc = desc;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neighborbook);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "NeighborBookActivity 시작: " + currentUserName);

        // Repository 초기화
        userRepository = new UserRepository(getApplication());

        // 툴바 설정
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 사용자 게임 진행상황 로드
        loadUserGameStatus();
    }

    private void loadUserGameStatus() {
        new Thread(() -> {
            try {
                User currentUser = userRepository.getUserById(currentUserId);

                runOnUiThread(() -> {
                    if (currentUser != null) {
                        // 🎯 해제된 스토리 수 = 완료된 스토리 + 1 (현재 진행 가능한 스토리)
                        unlockedStoryCount = currentUser.getTotalCompletedStories() + 1;
                        Log.d(TAG, "✅ 사용자 정보 로드 성공");
                        Log.d(TAG, "완료된 스토리: " + currentUser.getTotalCompletedStories());
                        Log.d(TAG, "해제된 스토리: " + unlockedStoryCount + "개");
                        setupNeighborList();
                    } else {
                        Log.e(TAG, "❌ 사용자 정보 없음 - 기본값 사용");
                        unlockedStoryCount = 1;
                        setupNeighborList();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 게임 상태 로드 실패", e);
                runOnUiThread(() -> {
                    unlockedStoryCount = 1;
                    setupNeighborList();
                });
            }
        }).start();
    }

    private void setupNeighborList() {
        Log.d(TAG, "이웃 목록 설정: " + unlockedStoryCount + "개 스토리 해제됨");

        // 전체 이웃 데이터 준비 (9명)
        List<Neighbor> allNeighbors = new ArrayList<>();
        allNeighbors.add(new Neighbor(R.drawable.neighbor1, "편의점 직원 배수연",
                "첫 번째로 만난 배수연양! 나이는 22세, 활발한 성격입니다."));
        allNeighbors.add(new Neighbor(R.drawable.neighbor2, "햄버거 가게 직원 조재영",
                "두 번째로 만난 조재영양! 나이는 23세, 친절하게 키오스크 사용법을 알려준 대학생입니다. 소프트웨어를 전공해요!"));
        allNeighbors.add(new Neighbor(R.drawable.neighbor3, "친절한 옆집 이웃 이예솔",
                "세 번째로 만난 이예솔양, 게임 광으로, 마인크래프트 게임과 가위바위보 게임을 좋아해요!"));
        allNeighbors.add(new Neighbor(R.drawable.neighbor4, "공원에서 만난 김다민",
                "네 번째로 만난 김다민양, 애완견 토리를 12년째 키우고 있는 견주입니다!"));
        allNeighbors.add(new Neighbor(R.drawable.neighbor5, "옆집 아이 마루",
                "다섯 번째로 만난 마루양, 소문으로는 강아지였다가 사람이 됐다고 하는데, 진실은 아무도 몰라요!"));
        allNeighbors.add(new Neighbor(R.drawable.neighbor6, "슈퍼 직원 정윤",
                "여섯 번째로 만난 정윤양, 친절한 옆집 이웃 이예솔이 가장 좋아하는 동생으로, 무척 귀엽고 엉뚱합니다."));
        allNeighbors.add(new Neighbor(R.drawable.neighbor7, "일본인 유학생 야마다",
                "일곱 번째로 만난 야마다군, 일본인 고등학생으로, 잠깐 한국에 놀러왔어요! 게임을 무척 잘하고, 잘생겼어요."));
        allNeighbors.add(new Neighbor(R.drawable.neighbor8, "버스 정류장에서 만난 정새연",
                "여덟 번째로 만난 새연양, 기억력이 매우 좋으며, 피씨방을 좋아해 매일 버스를 타고 방문합니다."));
        allNeighbors.add(new Neighbor(R.drawable.neighbor9, "사랑하는 손녀 김토리",
                "아홉 번째로 만난 토리양, 귀여운 외모를 가진 12살 소녀로, 10살, 12살 터울의 언니들이 있습니다."));

        // 컨테이너에 동적 Inflate
        LinearLayout container = findViewById(R.id.llContainer);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < allNeighbors.size(); i++) {
            View item = inflater.inflate(R.layout.neighbor_item, container, false);

            ImageView iv = item.findViewById(R.id.ivNeighborImage);
            TextView name = item.findViewById(R.id.tvNeighborName);
            TextView desc = item.findViewById(R.id.tvNeighborDesc);
            Button play = item.findViewById(R.id.btnPlayNeighbor);

            if (i < unlockedStoryCount) {
                // 🔓 UNLOCKED - 해제된 스토리
                Neighbor neighbor = allNeighbors.get(i);
                iv.setImageResource(neighbor.imageRes);
                name.setText(neighbor.name);
                desc.setText(neighbor.desc);
                play.setText("게임을 진행해주세요!");
                play.setEnabled(true);

                final int storyNumber = i + 1;
                play.setOnClickListener(v -> {
                    Log.d(TAG, "스토리 " + storyNumber + " 클릭");
                    Intent intent = new Intent(this, StoryActivity.class);
                    intent.putExtra("userId", currentUserId);
                    intent.putExtra("userName", currentUserName);
                    startActivity(intent);
                    finish(); // NeighborBookActivity 종료하고 StoryActivity로 이동
                });
            } else {
                // 🔒 LOCKED - 잠긴 스토리
                iv.setImageResource(R.drawable.ic_lock);
                name.setText("???");
                desc.setText("이전 스토리를 완료하면 해제됩니다.");
                play.setText("잠겨있음");
                play.setEnabled(false);
            }

            container.addView(item);
        }

        Log.d(TAG, "이웃 목록 설정 완료: " + allNeighbors.size() + "개 아이템");
    }
}