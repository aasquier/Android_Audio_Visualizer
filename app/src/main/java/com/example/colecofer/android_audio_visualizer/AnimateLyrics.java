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
    private static final int OPACITY_UPDATE_INC = 10; //Amount of opacity to add each time update is called

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
    private int counter = 0;


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
        float lyricDisplayTime = rawLyricsList.get(this.rawLyricsIndex).first - LYRIC_DISPLAY_OFFSET;
        float currentTime = VisualizerActivity.mediaPlayer.getCurrentPosition();
        int numWordsInLyricSegment = this.rawLyricsList.get(this.rawLyricsIndex).second.length;

        if (currentTime >= lyricDisplayTime && this.rawLyricsIndex < this.sizeOfRawLyricsList) {
            this.currentLyricsList = new ArrayList<>();

            //Check if there are more lyrics after this one
            //TODO: Check for null value in passed rawLyrics (shouldn't have to subtract one)
            if(this.rawLyricsIndex + 1 < (this.sizeOfRawLyricsList - 1)) {

                //Populate currentLyricsList with words in the current lyric segment
                for (int i = 0; i < numWordsInLyricSegment; ++i) {
                    SpannableString word = new SpannableString(rawLyricsList.get(this.rawLyricsIndex).second[i]);
                    this.currentLyricsList.add(new Pair<>(word, 0x00FFFFFF));
                }

                this.rawLyricsIndex += 1; //Index to the next lyric
            }
            this.lyricIndex = 0;
        }
        this.updateOpacity(numWordsInLyricSegment);
    }

    /**
     * Update the opacity of each word one at a time
     * @param numWordsInLyricSegment
     */
    void updateOpacity(int numWordsInLyricSegment) {
        List<SpannableString> lyricsToDisplay = new ArrayList<>();

        //Check that there are still lyrics to display
//        if (this.rawLyricsIndex < this.sizeOfRawLyricsList) {
            if (this.lyricIndex < numWordsInLyricSegment) {

            //Alter the opacity one word at a time
            for (int i = 0; i <= this.lyricIndex && i < numWordsInLyricSegment; ++i) {
//                SpannableString word = new SpannableString(rawLyricsList.get(this.rawLyricsIndex).second[i]);
                SpannableString word = new SpannableString(currentLyricsList.get(i).first);
                int colorSpan = rawLyricsList.get(this.rawLyricsIndex).first;

                if (counter % 100 == 0) {
                    int opacity = Color.alpha(colorSpan) + OPACITY_UPDATE_INC;
                    String updatedColor = String.format("%02xFFFFFF", opacity);
                    Log.d("test", "colorint: " + Long.parseLong(updatedColor, 16));
                    Log.d("test", updatedColor + ", opacity: " + opacity);
                    int colorAsInt = (int) Integer.parseInt(updatedColor, 16);

                    word.setSpan(new ForegroundColorSpan(colorAsInt), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    currentLyricsList.set(i, new Pair<>(word, colorSpan));
                }
                ++counter;

//                int updatedColor = opacity + Color.red(colorSpan) + Color.green(colorSpan) + Color.blue(colorSpan); // Not sure if this works as expected
//                word.setSpan(new ForegroundColorSpan(updatedColor), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //Create one string to display the lyrics
            for (Pair<SpannableString, Integer> lyric: this.currentLyricsList) {
                SpannableString word = lyric.first;
                lyricsToDisplay.add(word);
            }

            //Increment to the next word
            this.lyricIndex += 1;
            this.displayLyrics(lyricsToDisplay);
        }

    }


    /**
     * Takes an array of lyrics and displays them to
     * the lyric animation TextView.
     * @param lyrics String array of lyrics (word by word)
     */
    private void displayLyrics(List<SpannableString> lyrics) {
        int wordsAmt = lyrics.size();
        //String lyricsToDisplay = "";
        for (int i = 0; i < wordsAmt; ++i) {
            this.lyricsTextView.setText(lyrics.get(i));
            //this.lyricsTextView.append(lyrics.get(i));
            //lyricsToDisplay += " " + lyrics.get(i);
        }
        //Log.d("test", lyricsToDisplay);
        //this.lyricsTextView.setText(lyricsToDisplay);
    }

}




//SpannableString example
//        //Setup the text and colors
//        Spannable word = new SpannableString("Hah, sika than your average\n");
//
//        //The first two value of the hex are opacity... So perhaps we could alter these to fade them in and out... ?
//        word.setSpan(new ForegroundColorSpan(0x50FFFFFF), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        lyricsTextView.setText(word);
//
//        Spannable word1 = new SpannableString("Poppa twist cabbage off instinct");
//        word1.setSpan(new ForegroundColorSpan(Color.RED), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        lyricsTextView.append(word1);



//Code to display second line
//                //Check if the lyrics are close enough so that we can display them at the same time
//                if (rawLyricsList.get(this.lyricIndex + 1).first - rawLyricsList.get(this.lyricIndex).first
//                        < DISPLAY_MULTILINE_PROXIMITY) {
//
//                    //Add the second line of lyrics into the to be displayed list
//                    for (String lyric : rawLyricsList.get(this.lyricIndex).second) {
//                        SpannableString lyricSpan = new SpannableString(lyric);
//                        lyricsToDisplay.add(lyricSpan);
//                    }
//                    this.lyricIndex += 1;
//                }