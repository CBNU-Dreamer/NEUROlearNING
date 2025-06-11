package com.example.neurolearning.ui;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * 앱 전역에서 TextToSpeech를 간단히 사용할 수 있는 헬퍼 클래스
 */
public class TtsHelper implements TextToSpeech.OnInitListener {

    private static TtsHelper instance;
    private final Context appContext;
    private TextToSpeech tts;
    private boolean ready = false;

    private TtsHelper(Context context) {
        // TTS 초기화
        this.appContext = context.getApplicationContext();
        tts = new TextToSpeech(appContext, this);
    }

    /**
     * 인스턴스 가져오기 (최초 호출 시 생성됨)
     */
    public static synchronized TtsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TtsHelper(context);
        }
        return instance;
    }

    /**
     * TTS 초기화 콜백
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.2f);
            tts.setSpeechRate(1.0f);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 초기화 실패 토스트
                Toast.makeText(
                        appContext,
                        "TTS: 해당 언어를 지원하지 않습니다.",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                ready = true;
            }
        } else {
            Toast.makeText(
                    appContext,
                    "TTS 초기화에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * 준비되었으면 즉시 읽어줍니다.
     */
    public void speak(String text) {
        if (ready && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_HELPER");
        }
    }

    /**
     * 리소스 해제
     */
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
            instance = null;
            ready = false;
        }
    }
}
