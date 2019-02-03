package com.example.colecofer.android_audio_visualizer;

import android.util.Pair;

import java.util.ArrayDeque;

public class Utility {

    private static final float MAX_DB_LEVEL = 170.0f;
    private static final long REFRESH_DECIBEL_TIME = 9L;
    private static final float MAX_DECIBEL_TIME = 1.0f;

    /** Takes the real and imaginary parts of an FFT frequency bin and returns the decibels for that bin. */
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
     * @param newDB
     * @param dbHistory
     */
    static Pair<Long, Boolean> updateDbHistory(double newDB, ArrayDeque<Float> dbHistory, long previousUpdateTime) {
        Pair<Long, Boolean> isTime = isTimeToUpdate(previousUpdateTime);

        if (isTime.second == true) {
            float dbRatio = (float) newDB / MAX_DB_LEVEL;
            dbRatio = dbRatio > MAX_DECIBEL_TIME ? MAX_DECIBEL_TIME : dbRatio;
            dbHistory.addFirst(dbRatio);
            dbHistory.removeLast();
        }
        return isTime;
    }

    /**
     * Checks if it has been our predefined interval since last dB record update
     * @param previousUpdateTime
     * @return
     */
    static Pair<Long, Boolean> isTimeToUpdate(long previousUpdateTime) {
        Boolean success;
        Long currentTime = System.currentTimeMillis();
        if(previousUpdateTime + REFRESH_DECIBEL_TIME <= currentTime) {
            previousUpdateTime = currentTime;
            success = true;
        } else {
            success = false;
        }
        return new Pair(previousUpdateTime, success);
    }

}