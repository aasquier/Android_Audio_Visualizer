package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestVisualizerActivity extends AppCompatActivity {

    private GLSurfaceView visualizerSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_visualizer);

        //Create Surface view, assign the renderer, and set it as the content view
        visualizerSurfaceView = new GLSurfaceView(this);
        visualizerSurfaceView.setRenderer(new TestVisualizerRenderer());
        setContentView(visualizerSurfaceView);
    }
}
