package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LyricsView extends RelativeLayout {

    View rootView;
    TextView textView;

    //Used when created programmatically in java
    public LyricsView(Context context) {
        super(context);
        init(context);
    }

    //Used when created with xml
    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //Set up for view
    private void init(Context context) {
//        rootView = inflate(context, this);
    }


}
