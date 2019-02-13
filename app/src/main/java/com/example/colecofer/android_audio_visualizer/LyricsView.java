package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_PADDING;

public class LyricsView extends View {

        TextView test;

    public LyricsView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.test = new TextView(context);
        this.test.setText("THIS IS A TEST");

        this.getLayoutParams().width = (int) (screenWidth * 0.8);
        this.getLayoutParams().height = 400;
        this.setPadding(LEFT_PADDING, screenHeight / 3, RIGHT_PADDING, 10);
    }

    public void init() {

    }

}