package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.colecofer.android_audio_visualizer.Constants.LYRICS_TEXT_SIZE;

public class AnimateLyrics {

    private Typeface lyricsTypeface;

    static TextView lyrics;
    static ViewGroup.MarginLayoutParams lyricsParams;


    public AnimateLyrics(Context context) {
        lyricsTypeface = ResourcesCompat.getFont(context, R.font.sofiaproextralight);
        lyrics = new TextView(context);
        lyrics.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_TEXT_SIZE);
        lyrics.setTypeface(lyricsTypeface);

        //Setup the text and colors
        Spannable word = new SpannableString("Hah, sika than your average\n");

        //The first two value of the hex are opacity... So perhaps we could alter these to fade them in and out... ?
        word.setSpan(new ForegroundColorSpan(0x50FFFFFF), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        lyrics.setText(word);

        Spannable word1 = new SpannableString("Poppa twist cabbage off instinct");
        word1.setSpan(new ForegroundColorSpan(Color.RED), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        lyrics.append(word1);

        lyricsParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        lyrics.setLayoutParams(lyricsParams);
        lyrics.setPadding(100, 800, 100, 300);
    }

}
