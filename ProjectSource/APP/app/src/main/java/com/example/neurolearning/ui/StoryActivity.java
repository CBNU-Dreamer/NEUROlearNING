package com.example.neurolearning.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.neurolearning.R;
import com.example.neurolearning.data.User;
import com.example.neurolearning.data.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {
    private static final String TAG = "StoryActivity";

    private UserRepository userRepository;
    private String currentUserId;
    private String currentUserName;
    private int unlockedStoryCount = 1; // 해제된 스토리 수

    // 하단 네비게이션 바 버튼들
    private Button btnDictionary;
    private Button btnUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // 사용자 정보 가져오기
        currentUserId = getIntent().getStringExtra("userId");
        currentUserName = getIntent().getStringExtra("userName");

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "StoryActivity 시작: " + currentUserName + " (ID: " + currentUserId + ")");

        // Repository 초기화
        userRepository = new UserRepository(getApplication());

        // 하단 네비게이션 바 초기화
        initBottomNavigation();

        // 사용자 게임 현황 로드
        loadUserGameStatus();
    }

    // 하단 네비게이션 바 초기화
    private void initBottomNavigation() {
        btnDictionary = findViewById(R.id.btnDictionary);
        btnUserInfo = findViewById(R.id.btnUserInfo);

        // 이웃 사전 버튼
        btnDictionary.setOnClickListener(v -> {
            Intent intent = new Intent(StoryActivity.this, NeighborBookActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("userName", currentUserName);
            startActivity(intent);
        });

        // 사용자 정보 버튼
        btnUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(StoryActivity.this, UserInfoActivity.class);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        });
    }

    private void loadUserGameStatus() {
        new Thread(() -> {
            try {
                User currentUser = userRepository.getUserById(currentUserId);

                runOnUiThread(() -> {
                    if (currentUser != null) {
                        // 🎯 현재 스토리 = 완료된 스토리 + 1 (다음 플레이할 스토리)
                        unlockedStoryCount = currentUser.getTotalCompletedStories() + 1;
                        Log.d(TAG, "✅ 사용자 정보 로드 성공");
                        Log.d(TAG, "완료된 스토리: " + currentUser.getTotalCompletedStories());
                        Log.d(TAG, "현재 진행 스토리: " + currentUser.getCurrentStory());
                        Log.d(TAG, "해제된 스토리 수: " + unlockedStoryCount);
                        setupStoryList();
                    } else {
                        Log.e(TAG, "❌ 사용자 정보 없음 - 기본값 사용");
                        unlockedStoryCount = 1;
                        setupStoryList();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "❌ 사용자 게임 상태 로드 실패", e);
                runOnUiThread(() -> {
                    unlockedStoryCount = 1;
                    setupStoryList();
                });
            }
        }).start();
    }

    private void setupStoryList() {
        LinearLayout container = findViewById(R.id.storyContainer);
        container.removeAllViews();

        List<Neighbor> neighbors = new ArrayList<>();
        neighbors.add(new Neighbor("편의점 직원 배수연", "첫 번째 이야기"));
        neighbors.add(new Neighbor("햄버거 가게 직원 조재영", "두 번째 이야기"));
        neighbors.add(new Neighbor("친절한 옆집 이웃 이예솔", "세 번째 이야기"));
        neighbors.add(new Neighbor("공원에서 만난 김다민", "네 번째 이야기"));
        neighbors.add(new Neighbor("옆집 아이 마루", "다섯 번째 이야기"));
        neighbors.add(new Neighbor("슈퍼 직원 정윤", "여섯 번째 이야기"));
        neighbors.add(new Neighbor("일본인 유학생 야마다", "일곱 번째 이야기"));
        neighbors.add(new Neighbor("버스 정류장에서 만난 정새연", "여덟 번째 이야기"));
        neighbors.add(new Neighbor("사랑하는 손녀 김토리", "아홉 번째 이야기"));

        for (int i = 0; i < neighbors.size(); i++) {
            Neighbor neighbor = neighbors.get(i);

            View itemView = getLayoutInflater().inflate(R.layout.story_item, container, false);
            ConstraintLayout root = itemView.findViewById(R.id.storyItemRoot);
            TextView subtitle = itemView.findViewById(R.id.textSubtitle);
            TextView title = itemView.findViewById(R.id.textStoryTitle);
            ImageView lockIcon = itemView.findViewById(R.id.imageLock);

            title.setText(neighbor.getStorytext());

            if (i < unlockedStoryCount) {
                // 해제된 스토리
                subtitle.setText(neighbor.getNeighbortext());
                lockIcon.setVisibility(View.GONE);

                // 현재 진행 가능한 스토리는 노란색 강조
                boolean isCurrentStory = (i == unlockedStoryCount - 1);
                root.setBackground(createBackground(
                        isCurrentStory ? "#FFFDE7" : "#FFFFFF",
                        isCurrentStory ? 4 : 2,
                        isCurrentStory ? "#000000" : "#E0E0E0"
                ));

                final int storyNumber = i + 1;
                itemView.setOnClickListener(v -> {
                    Class<?> targetClass = getStoryClass(storyNumber);
                    if (targetClass != null) {
                        Intent intent = new Intent(this, targetClass);
                        intent.putExtra("userId", currentUserId);
                        intent.putExtra("userName", currentUserName);
                        intent.putExtra("storyNumber", storyNumber);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "해당 스토리는 아직 준비 중입니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                // 잠긴 스토리
                subtitle.setText("???");
                lockIcon.setVisibility(View.VISIBLE);
                root.setBackground(createBackground("#FFFFFF", 2, "#E0E0E0"));
                itemView.setOnClickListener(v ->
                        Toast.makeText(this, "이 스토리를 진행하시려면 이전 스토리를 완료해야 합니다.", Toast.LENGTH_SHORT).show());
            }

            container.addView(itemView);
        }

        Log.d(TAG, "스토리 목록 설정 완료: " + unlockedStoryCount + "개 해제됨");
    }

    // 현재/기본 배경을 동적으로 생성
    private GradientDrawable createBackground(String fillColor, int strokeWidth, String strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(fillColor));
        drawable.setCornerRadius(32f);
        drawable.setStroke(strokeWidth, Color.parseColor(strokeColor));
        return drawable;
    }

    // 스토리 번호에 따라 해당 Activity 클래스 반환
    private Class<?> getStoryClass(int storyNumber) {
        switch (storyNumber) {
            case 1:
                return Story1Activity.class;
            case 2:
                return Story2Activity.class;
            case 3:
                return Story3Activity.class;
            // 4~9번 스토리는 아직 구현되지 않았으므로 임시로 Story2Activity 사용
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Story2Activity.class; // 임시로 Story2Activity 사용
            default:
                return null;
        }
    }

    // 액티비티 재개 시 사용자 상태 새로고침
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - 사용자 상태 새로고침");
        loadUserGameStatus();
    }

    // 내부 데이터 모델
    private static class Neighbor {
        private final String neighbortext;
        private final String storytext;

        public Neighbor(String neighbortext, String storytext) {
            this.neighbortext = neighbortext;
            this.storytext = storytext;
        }

        public String getNeighbortext() {
            return neighbortext;
        }

        public String getStorytext() {
            return storytext;
        }
    }
}