package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.colecofer.android_audio_visualizer.Constants.MAX_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_DECIBEL_RATIO;
import static com.example.colecofer.android_audio_visualizer.Constants.REFRESH_DECIBEL_TIME;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class Utility {

    private Context context;

      /**
       * Pass context for connecting Resource
       *
       *context* *
       */
      public Utility(Context context) {
            this.context = context;
      }

    /**
     * Takes the real and imaginary parts of an FFT frequency bin and returns the decibels for that bin.
     */
    static double getDBs(byte real, byte imaginary, int n) {
        /** Bit shifting to translate our bytes into doubles */
        double y = (real | imaginary << 8) / 32768.0;

        /** Finds the Root Mean Square and transforms it into dBs, ensures it does not pass
         *  the Log10 a 0.0 which would return Inf */
        if (y != 0.0) {
            return Math.abs(10.0 * Math.log10((4.0 * y * y) / (n * n)));
        } else {
            return 0.0;
        }
    }

    /**
     * Keeps a record of recent dBs as large as the screen is tall. It removes the last record and
     * removes the oldest record. If the current dB level exceeds our max setting it uses the max
     *
     * @param newDecibelLevel
     */
    static Pair<Long, Boolean> updateDecibelHistory(double newDecibelLevel, long previousUpdateTime) {
        Pair<Long, Boolean> isTimeToUpdate = isTimeToUpdate(previousUpdateTime);

        /** A check to ensure that the current time has exceeded the desired refresh time */
        if (isTimeToUpdate.second) {
            float newDbRatio = (float) newDecibelLevel / MAX_DB_LEVEL;
            newDbRatio = newDbRatio > MAX_DECIBEL_RATIO ? MAX_DECIBEL_RATIO : newDbRatio;
            /** Update the decibel history with the current decibel level */
            decibelHistory.addFirst(newDbRatio);
            decibelHistory.removeLast();
        }
        return isTimeToUpdate;
    }

    /**
     * Checks if it has been our predefined interval since last dB record update
     *
     * @param previousUpdateTime
     * @return
     */
    static Pair<Long, Boolean> isTimeToUpdate(long previousUpdateTime) {
        Boolean success;
        Long currentTime = System.currentTimeMillis();
        if (previousUpdateTime + REFRESH_DECIBEL_TIME <= currentTime) {
            previousUpdateTime = currentTime;
            success = true;
        } else {
            success = false;
        }
        return new Pair(previousUpdateTime, success);
    }

    /**
     * Convert a glsl file into  a string for each visualizer
     *
     * @param id
     */

    public String getStringFromGLSL(@RawRes int id) {
        String str;

        try {
            Resources res = context.getResources();
            InputStream is = res.openRawResource(id);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = is.read();
            }
            str = byteArrayOutputStream.toString();
            is.close();
        } catch (IOException e) {
            Log.d("GLSL", "Failed to convert GLSL file into a proper String. " + e.getMessage());
            str = "";
        }

        return str;
    }
}