package com.example.neurolearning.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.neurolearning.R;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {

    private int step = 8; // 현재 진행 중인 스토리 단계 (1부터 시작)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        LinearLayout container = findViewById(R.id.storyContainer);

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
        neighbors.add(new Neighbor("???", "열 번째 이야기"));

        for (int i = 0; i < neighbors.size(); i++) {
            Neighbor neighbor = neighbors.get(i);

            View itemView = getLayoutInflater().inflate(R.layout.story_item, container, false);
            ConstraintLayout root = itemView.findViewById(R.id.storyItemRoot);
            TextView subtitle = itemView.findViewById(R.id.textSubtitle);
            TextView title = itemView.findViewById(R.id.textStoryTitle);
            ImageView lockIcon = itemView.findViewById(R.id.imageLock);

            title.setText(neighbor.getStorytext());

            if (i < step) {
                subtitle.setText(neighbor.getNeighbortext());
                lockIcon.setVisibility(View.GONE);

                // 현재 스토리는 노란색 강조
                root.setBackground(createBackground(
                        i == step - 1 ? "#FFFDE7" : "#FFFFFF",
                        i == step - 1 ? 4 : 2,
                        i == step - 1 ? "#000000" : "#E0E0E0"
                ));

                int index = i;
                itemView.setOnClickListener(v -> {
                    Class<?> targetClass = getStoryClass(index);
                    if (targetClass != null) {
                        Intent intent = new Intent(this, targetClass);
                        startActivity(intent);
                    }
                });

            } else {
                subtitle.setText("???");
                lockIcon.setVisibility(View.VISIBLE);
                root.setBackground(createBackground("#FFFFFF", 2, "#E0E0E0"));
                itemView.setOnClickListener(v ->
                        Toast.makeText(this, "이 스토리를 진행하시려면 이전 스토리를 완료해야 합니다.", Toast.LENGTH_SHORT).show());
            }

            container.addView(itemView);
        }
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
    private Class<?> getStoryClass(int index) {
        switch (index) {
            case 0: return Story2Activity.class;
            case 1: return Story2Activity.class;
            case 2: return Story3Activity.class;
            case 3: return Story2Activity.class;
            case 4: return Story2Activity.class;
            case 5: return Story2Activity.class;
            case 6: return Story2Activity.class;
            case 7: return Story2Activity.class;
            case 8: return Story2Activity.class;
            case 9: return Story2Activity.class;
            default: return null;
        }
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
