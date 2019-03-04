package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.colecofer.android_audio_visualizer.Constants.LYRICS_TEXT_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.LYRIC_DISPLAY_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.PERCENTAGE_FROM_TOP;


/**
 * This class creates lyric animations using Spannable Strings.
 * These are useful because you can treat words individually,
 * where you can't with normal Strings.
 * The animation consists of multiple Spannable Strings kept
 * inside of a TextView.
 */
public class AnimateLyrics {
    private int opacityUpdateInc = 20; //Amount of opacity to add each time update is called
    private int opacityUpdateDec = 0; //Amount of opacity to add each time update is called

    static TextView lyricsTextView;
    static ViewGroup.MarginLayoutParams lyricsParams;

    private Typeface lyricsTypeface;
    private ArrayList<Pair<Integer, String[]>> rawLyricsList;         //Holds the lyrics as plain Strings with their timestamps to be displayed
    private ArrayList<Pair<SpannableString, Integer>> currentLyricsList; //Lyrics that are actively being displayed with their curr opacity
    private int sizeOfRawLyricsList = 0;                                //Total amount of lyric segments
    private int rawLyricsIndex = 0;
    private int lyricIndex;
    private int screenWidth;
    private int screenHeight;
    private boolean timeToFadeAwayFlag;
    private float lyricEndTime;           //Amount of milliseconds that the current segment will be displayed for
    private int numWordsInLyricSegment;       //Amount of words in the current lyric segment


    /**
     * Animate Lyrics Constructor
     * @param context Context for reference
     * @param screenWidth Screen width in pixels
     * @param screenHeight Screen height in pixels
     * @param lyricList ArrayList of lyrics with time stamps
     */
    public AnimateLyrics(Context context, int screenWidth, int screenHeight, ArrayList<Pair<Integer, String[]>> lyricList) {

        //Lyric Text Setup
        this.lyricsTypeface = ResourcesCompat.getFont(context, R.font.sofiaprobold);
        this.lyricsTextView = new TextView(context);
        this.lyricsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_TEXT_SIZE);
        this.lyricsTextView.setTypeface(lyricsTypeface);
        this.lyricsTextView.setTextColor(Color.WHITE);
        this.lyricsParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        this.lyricsTextView.setLayoutParams(lyricsParams);

        //Lyric Containers
        this.rawLyricsList = (ArrayList<Pair<Integer, String[]>>) lyricList.clone();
        this.sizeOfRawLyricsList = this.rawLyricsList.size();
        this.rawLyricsIndex = 0;
        this.lyricIndex = 0;
        this.currentLyricsList = new ArrayList<>();

        this.timeToFadeAwayFlag = false;

        //Screen dimensions
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Set the height as a percentage of the screen height
        int height = (int) (this.screenHeight * PERCENTAGE_FROM_TOP);
        this.lyricsTextView.setPadding(100, height, 100, 100);
    }


    /**
     * Displays the next set of lyrics according to the timestamps
     * TODO: Possibly take lyrics off screen if they sit around too long (like the end)
     */
    public void update() {

        //Calc time to display the lyric
        this.lyricEndTime = rawLyricsList.get(this.rawLyricsIndex).first /* - LYRIC_DISPLAY_OFFSET*/;
        float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();
        this.numWordsInLyricSegment = this.rawLyricsList.get(this.rawLyricsIndex).second.length;

        //This code will execute when it's time to display a new lyric segment
        if (currentTime >= lyricEndTime && this.rawLyricsIndex < this.sizeOfRawLyricsList) {
            this.currentLyricsList = new ArrayList<>();

            //Check if there are more lyrics after this one
            if(this.rawLyricsIndex < (this.sizeOfRawLyricsList - 1)) {

                //Populate currentLyricsList with words in the current lyric segment
                for (int i = 0; i < numWordsInLyricSegment; ++i) {
                    SpannableString word = new SpannableString(rawLyricsList.get(this.rawLyricsIndex).second[i] + " ");
                    this.currentLyricsList.add(new Pair<>(word, 0x00FFFFFF));
                }

                //Index to the next lyric
                this.rawLyricsIndex += 1;

                //Update variable to signal to stop the fade out animation
                this.timeToFadeAwayFlag = false;

            }
            this.lyricIndex = 0;
        }

        this.updateOpacity();
    }

    /**
     * Update the opacity of each word one at a time
     */
    void updateOpacity() {
        //List<SpannableString> lyricsToDisplay = new ArrayList<>();
        int currentLyricListSize = currentLyricsList.size();

        //Check that there are still lyrics to display
        if (this.lyricIndex < currentLyricListSize) {

            //Calculate the duration that the current lyrics will be displayed
            float lyricSegmentDisplayDuration = (255 / opacityUpdateInc) * 18;
            if (this.rawLyricsIndex + 1 < rawLyricsList.size()) {
                lyricSegmentDisplayDuration = rawLyricsList.get(this.rawLyricsIndex + 1).first - rawLyricsList.get(this.rawLyricsIndex).first;
            }

            this.lyricEndTime = rawLyricsList.get(this.rawLyricsIndex).first;  // - LYRIC_DISPLAY_OFFSET;
            float animationDuration = lyricSegmentDisplayDuration * 1/2;
            float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();
            float timeToStartFadeAway = lyricEndTime - animationDuration;

            //Check if it's time to start the fadeaway animation
            if (currentTime >= timeToStartFadeAway && this.timeToFadeAwayFlag == false) {
                Log.d("test", "timeToFadeAwayFlag switched to true");
                this.lyricIndex = 0;
                this.timeToFadeAwayFlag = true;
            }

            //Alter the opacity one word at a time
            for (int i = 0; i <= this.lyricIndex && i < currentLyricListSize; ++i) {
                SpannableString word = new SpannableString(currentLyricsList.get(i).first);
                int colorSpan = currentLyricsList.get(i).second;
                int opacity = Color.alpha(colorSpan);

                //Update the opacity accordingly if we are fading in or out
                if (this.timeToFadeAwayFlag == true) {
                    opacity += this.opacityUpdateDec;
                    if (opacity <= 0) opacity = 0;
                } else {
                    opacity += this.opacityUpdateInc;
                    if (opacity >= 255) opacity = 255;
                }

                //Construct the updated color into hex
                String updatedColor = String.format("#%02xFFFFFF", opacity);
                int colorAsInt = Color.parseColor(updatedColor);

                //Construct a new Span for the previous word (because ArrayLists are immutable)
                word.setSpan(new ForegroundColorSpan(colorAsInt), 0, word.length(), Spannable.SPAN_COMPOSING); //Try some span flags out (SPAN_INCLUSIVE_EXCLUSIVE)
                currentLyricsList.set(i, new Pair<>(word, colorAsInt));
            }

            //Clear the string
            this.lyricsTextView.setText(new SpannableString(""));

            //Add them to the textview
            for (Pair<SpannableString, Integer> lyric: this.currentLyricsList) {
                this.lyricsTextView.append(lyric.first);
            }

            // Increment to the next word
            if (this.lyricIndex < (currentLyricListSize - 1)) {
                this.lyricIndex += 1;
            }
        }
    }


//    /**
//     * Update the opacity of each word one at a time
//     */
//    void fadeInAnimation() {
//        int currentLyricListSize = currentLyricsList.size();
//
//        //Check that there are still lyrics to display
//        if (this.lyricIndex < currentLyricListSize) {
//
//        //            float lyricSegmentDisplayDuration = (255 / opacityUpdateInc) * 18;
//        //            if (this.rawLyricsIndex + 1 < rawLyricsList.size()) {
//        //                lyricSegmentDisplayDuration = rawLyricsList.get(this.rawLyricsIndex + 1).first - rawLyricsList.get(this.rawLyricsIndex).first;
//        //            }
//        //            this.lyricEndTime = rawLyricsList.get(this.rawLyricsIndex).first;  // - LYRIC_DISPLAY_OFFSET;
//        //            float animationDuration = lyricSegmentDisplayDuration * 1/3;
//        //            float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();
//        //            float timeToStartFadeAway = lyricEndTime - animationDuration;
//        //
//        //            //Check if it's time to start the fadeaway animation
//        //            if (currentTime >= timeToStartFadeAway && this.timeToFadeAwayFlag == false) {
//        //                Log.d("test", "timeToFadeAwayFlag switched to true");
//        //                this.lyricIndex = 0;
//        //                this.timeToFadeAwayFlag = true;
//        //            }
//
//            //Alter the opacity one word at a time
//            for (int i = 0; i <= this.lyricIndex && i < currentLyricListSize; ++i) {
//                SpannableString word = new SpannableString(currentLyricsList.get(i).first);
//                int colorSpan = currentLyricsList.get(i).second;
//                int opacity = Color.alpha(colorSpan);
//
//                opacity += this.opacityUpdateInc;
//                if (opacity >= 255) opacity = 255;
//
//                //Construct the updated color into hex
//                String updatedColor = String.format("#%02xFFFFFF", opacity);
//                int colorAsInt = Color.parseColor(updatedColor);
//
//                //Construct a new Span for the previous word (because ArrayLists are immutable)
//                word.setSpan(new ForegroundColorSpan(colorAsInt), 0, word.length(), Spannable.SPAN_COMPOSING); //Try some span flags out (SPAN_INCLUSIVE_EXCLUSIVE)
//                currentLyricsList.set(i, new Pair<>(word, colorAsInt));
//            }
//
//            //Clear the string
//            this.lyricsTextView.setText(new SpannableString(""));
//
//            //Add them to the textview
//            for (Pair<SpannableString, Integer> lyric: this.currentLyricsList) {
//                this.lyricsTextView.append(lyric.first);
//            }
//
//            // Increment to the next word
//            if (this.lyricIndex < (currentLyricListSize - 1)) {
//                this.lyricIndex += 1;
//            }
//        }
//    }
//
//    /**
//     * Update the opacity of each word one at a time
//     */
//    void fadeOutAnimation() {
//        int currentLyricListSize = currentLyricsList.size();
//
//        //Check that there are still lyrics to display
//        if (this.lyricIndex < currentLyricListSize) {
//
//            //Alter the opacity one word at a time
//            for (int i = 0; i <= this.lyricIndex && i < currentLyricListSize; ++i) {
//                SpannableString word = new SpannableString(currentLyricsList.get(i).first);
//                int colorSpan = currentLyricsList.get(i).second;
//                int opacity = Color.alpha(colorSpan);
//
//                opacity += this.opacityUpdateDec;
//                if (opacity <= 0) opacity = 0;
//
//                //Construct the updated color into hex
//                String updatedColor = String.format("#%02xFFFFFF", opacity);
//                int colorAsInt = Color.parseColor(updatedColor);
//
//                //Construct a new Span for the previous word (because ArrayLists are immutable)
//                word.setSpan(new ForegroundColorSpan(colorAsInt), 0, word.length(), Spannable.SPAN_COMPOSING); //Try some span flags out (SPAN_INCLUSIVE_EXCLUSIVE)
//                currentLyricsList.set(i, new Pair<>(word, colorAsInt));
//            }
//
//            //Clear the string
//            this.lyricsTextView.setText(new SpannableString(""));
//
//            //Add them to the textview
//            for (Pair<SpannableString, Integer> lyric: this.currentLyricsList) {
//                this.lyricsTextView.append(lyric.first);
//            }
//
//            // Increment to the next word
//            if (this.lyricIndex < (currentLyricListSize - 1)) {
//                this.lyricIndex += 1;
//            }
//        }
//    }

}















