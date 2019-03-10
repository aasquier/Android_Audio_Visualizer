package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.colecofer.android_audio_visualizer.Constants.BOTTOM_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.DEMO_MODE;
import static com.example.colecofer.android_audio_visualizer.Constants.DISPLAY_MULTILINE_PROXIMITY;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.LYRICS_TEXT_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.LYRIC_DISPLAY_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_HEIGHT_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.PERCENT_FROM_TOP;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.SCROLL_LYRICS_SPEED;


/**
 * This class creates lyric animations using Spannable Strings.
 * These are useful because you can treat words individually,
 * where you can't with normal Strings.
 * The animation consists of multiple Spannable Strings kept
 * inside of a TextView.
 */
public class AnimateLyrics {
    private float opacityUpdateInc = 0.2f;  //Amount of opacity to add each time update is called
    private float opacityUpdateDec = -0.2f;

    static TextView lyricsTextView;
    static ViewGroup.MarginLayoutParams lyricsParams;

    private Typeface lyricsTypeface;
    private ArrayList<Pair<Integer, String[]>> rawLyricsList;         //Holds the lyrics as plain Strings with their timestamps to be displayed
    private int sizeOfRawLyricsList = 0;                              //Total amount of lyric segments
    private int rawLyricsIndex = 0;
    private int screenHeight;
    private float lyricEndTime;               //Amount of milliseconds that the current segment will be displayed for
    private int numWordsInLyricSegment;       //Amount of words in the current lyric segment

    List<String> lyricsToDisplay;             //Holds the current lyrics to display
    private float lyricTextViewOpacity;

    //Scrolling variables
    private int defaultHeightPadding;         //Default height that the lyricsTextView should be displayed at
    private int maxHeightPadding;             //Max height that the lyricsTextView can scroll too

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
        this.lyricTextViewOpacity = 0.0f;

        //Dirty hack to reduce timestamp times for biggie smalls because the timestamps are all slightly delayed
        for (int i = 0; i < this.rawLyricsList.size(); ++i) {
            Integer reducedTimeStamp = this.rawLyricsList.get(i).first - 1500;
            Pair<Integer, String[]> newPair = new Pair(reducedTimeStamp, this.rawLyricsList.get(i).second);
            this.rawLyricsList.set(i, newPair);
        }

        lyricsToDisplay = new ArrayList<>();

        //Screen dimensions
        this.screenHeight = screenHeight;

        //Set the default height as a percentage of the screen height
        this.defaultHeightPadding = (int) (this.screenHeight * PERCENT_FROM_TOP);
        updateHeightPadding(this.defaultHeightPadding);
        this.maxHeightPadding = this.defaultHeightPadding - MAX_HEIGHT_OFFSET;
    }

    /**
     * Displays the next set of lyrics according to the timestamps
     */
    public void update() {

        //Calc time to display the lyric
        this.lyricEndTime = rawLyricsList.get(this.rawLyricsIndex).first - LYRIC_DISPLAY_OFFSET;
        float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();
        this.numWordsInLyricSegment = this.rawLyricsList.get(this.rawLyricsIndex).second.length;

        //This code will execute when it's time to display a new lyric segment
        if (currentTime >= lyricEndTime && this.rawLyricsIndex < this.sizeOfRawLyricsList) {

            //Check if there are more lyrics after this one
            if(this.rawLyricsIndex < (this.sizeOfRawLyricsList - 1)) {

                //Populate lyricsToDisplay with the words in the lyric segment
                for (String item : rawLyricsList.get(this.rawLyricsIndex).second) {

                    //Replace tab characters only if in demo mode
                    if (DEMO_MODE == true) {
                        item = item.replace("\t", "\n");
                        lyricsToDisplay.add(item);
                    } else {
                        lyricsToDisplay.add(item);
                    }

                }

                //Only look to display multiple lines if we are not in demo mode
                if (DEMO_MODE == false) {

                    //Check if the lyrics are close enough so that we can display them at the same time
                    if (rawLyricsList.get(this.rawLyricsIndex + 1).first - rawLyricsList.get(this.rawLyricsIndex).first
                            < DISPLAY_MULTILINE_PROXIMITY) {
                        for (String item : rawLyricsList.get(this.rawLyricsIndex + 1).second) {
                            lyricsToDisplay.add(item);
                        }
                        this.rawLyricsIndex += 1;
                    }
                }
                //Index to the next lyric
                this.rawLyricsIndex += 1;
            }
            this.displayLyrics(lyricsToDisplay);
        }

        this.scrollTextView();
        this.updateOpacity();
    }


    /**
     * Takes an array of lyrics and displays them to
     * the lyric animation TextView.
     * @param lyrics String array of lyrics (word by word)
     */
    private void displayLyrics(List<String> lyrics) {
        int wordsAmt = lyrics.size();
        String lyricsToDisplay = "";
        this.lyricsTextView.setAlpha(0);
        for (int i = 0; i < wordsAmt; ++i) {
            if (DEMO_MODE == true) {
                lyricsToDisplay += lyrics.get(i) + " ";
            } else {
                lyricsToDisplay += " " + lyrics.get(i);
            }
        }
        this.lyricsTextView.setText(lyricsToDisplay);
        this.lyricsToDisplay.clear();
        this.updateHeightPadding(this.defaultHeightPadding);
        this.lyricTextViewOpacity = 0.0f;
    }

    /**
     * Animated the lyricsTextView in an upward scrolling motion.
     * Once new lyrics appear the position gets reset to the start of the textview.
     */
    void scrollTextView() {
        int currentHeightPadding = this.lyricsTextView.getPaddingTop();

        //Check if we have scrolled to the max height, and keep it there if so
        if (currentHeightPadding <= this.maxHeightPadding) {
            this.updateHeightPadding(this.maxHeightPadding);
        } else {
            //Decrement the lyrics padding to animate it scrolling upward
            this.updateHeightPadding(currentHeightPadding - SCROLL_LYRICS_SPEED);
        }

    }

    /**
     * A convient function to only update the height padding, while keeping the rest of the
     * padding defined by constants.
     * @param height the new height for the lyricsTextView to be displayed at.
     */
    void updateHeightPadding(int height) {
        this.lyricsTextView.setPadding(LEFT_PADDING, height, RIGHT_PADDING, BOTTOM_PADDING);
    }

    /**
     * Update the opacity of each word one at a time creating a fade in animation
     * as new lyrics get displayed onto the screen.
     */
    void updateOpacity() {

        float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();

        if (currentTime > this.lyricEndTime - 300) {
            this.lyricTextViewOpacity += opacityUpdateDec;

            if (this.lyricTextViewOpacity <= 0.0f) {
                this.lyricTextViewOpacity = 0.0f;
            }
        } else {
            this.lyricTextViewOpacity += opacityUpdateInc;

            if (this.lyricTextViewOpacity >= 1.0f) {
                this.lyricTextViewOpacity = 1.0f;
            }
        }
        this.lyricsTextView.setAlpha(this.lyricTextViewOpacity);
    }
}










